import java.security.PublicKey;
import java.util.*;


public class TxHandler {

    // current of unspent transaction outputs
    private UTXOPool utxoPool;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        this.utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool,
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        Set<UTXO> claimedUTXO = new HashSet<>();

        double sumOfInputValues = 0d;
        for (int inputIndex = 0; inputIndex < tx.getInputs().size(); inputIndex++) {
            Transaction.Input txInput = tx.getInput(inputIndex);

            // 1) make sure the input of our tx (which corresponds to a claimed "output" of _another_ tx)
            // is in the unused output pool
            UTXO candidateUTXO = new UTXO(txInput.prevTxHash, txInput.outputIndex);
            if (!utxoPool.contains(candidateUTXO)) {
                return false;
            }

            // 2) make sure the input is signed by whoever owns it
            // (ie whoever was given the amt as part of the prior tx output)
            Transaction.Output unusedOutput = utxoPool.getTxOutput(candidateUTXO);
            PublicKey ownerOfUnusedOutput = unusedOutput.address;
            byte[] rawDataToSign = tx.getRawDataToSign(inputIndex);
            if (!Crypto.verifySignature(ownerOfUnusedOutput, rawDataToSign, txInput.signature)) {
                return false;
            }

            // 3) make sure our tx hasn't already claimed this particular utxo on a prior loop iteration
            if (claimedUTXO.contains(candidateUTXO)) {
                return false;
            }
            claimedUTXO.add(candidateUTXO);

            // the output passes all checks so far; keep track of our running total
            sumOfInputValues += unusedOutput.value;
        }

        // sanity check num of claimed txos is right
        if (claimedUTXO.size() != tx.getInputs().size()) {
            throw new RuntimeException("Not all UTXOs are claimed?? " + claimedUTXO.size()
                    + " != " + tx.getInputs().size());
        }

        // 4) make sure all output values are non-negative
        double sumOfOutputValues = 0d;
        for (Transaction.Output txOutput : tx.getOutputs()) {
            if (txOutput.value < 0d) {
                return false;
            }
            sumOfOutputValues += txOutput.value;
        }

        // 5) net input value has to be more than or equal to net output value
        if (sumOfInputValues < sumOfOutputValues) {
            return false;
        }

        // passed all checks wheeee
        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     *
     * This will NOT process circular transaction references.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        List<Transaction> txToReturnAsValid = new ArrayList<>();

        // keep looping through the transactions, trying to add new ones, until we can no longer add any
        List<Transaction> txToValidateThisIteration = Arrays.asList(possibleTxs);
        while (true) {
            List<Transaction> validatedThisIteration = handleTxsLoop(txToValidateThisIteration);

            // if nothing new was validated this iteration, we're done
            if (validatedThisIteration.size() == 0) {
                break;
            }

            // anything newly validated goes into our global list of validated tx
            txToReturnAsValid.addAll(validatedThisIteration);

            // remove newly validated stuff from our list of not-yet-validated transaction
            // (these might be found to be valid next iteration, insofar as they might depend on
            // transactions we marked as valid _this_ iteration).
            Set<Transaction> txToAttemptNextIteration = new HashSet<>(txToValidateThisIteration);
            txToAttemptNextIteration.removeAll(validatedThisIteration);
            txToValidateThisIteration = new ArrayList<>(txToAttemptNextIteration);
        }
        return txToReturnAsValid.toArray(new Transaction[0]);
    }

    // do one iteration loop through the given transactions to see how many of them we can
    // process, given the current UTXO pool. Note that processing one block might involve calling
    // this method multiple times, so that we can process transactions that reference each other
    public List<Transaction> handleTxsLoop(List<Transaction> possibleTxs) {
        // get all transactions that are valid on their own
        List<Transaction> validInIsolationTx = new ArrayList<>();
        for (Transaction tx : possibleTxs) {
            if (isValidTx(tx)) {
                validInIsolationTx.add(tx);
            }
        }

        // iterate through all the tx's desired UTXOs (all should be in the pool, since the tx is valid)
        // whenever run into a transactions that tries to spend an already-encountered UTXO,
        // throw it away. The remaining transactions will be mutually consistent and (locally) maximal

        Set<UTXO> claimedUTXO = new HashSet<>();
        List<Transaction> acceptedTx = new ArrayList<>();

        for (Transaction tx : validInIsolationTx) {
            boolean shouldAcceptTx = true;

            Set<UTXO> txsUTXOs = new HashSet<>();
            for (int inputIndex = 0; inputIndex < tx.getInputs().size(); inputIndex++) {
                Transaction.Input txInput = tx.getInput(inputIndex);
                UTXO utxo = new UTXO(txInput.prevTxHash, txInput.outputIndex);
                if (claimedUTXO.contains(utxo)) {
                    shouldAcceptTx = false;
                    break;
                }
                txsUTXOs.add(utxo);
            }

            if (shouldAcceptTx) {
                claimedUTXO.addAll(txsUTXOs);
                acceptedTx.add(tx);
            }
        }

        // update the UTXO pool

        // remove claimed UTXOs...
        for (UTXO utxo : claimedUTXO) {
            utxoPool.removeUTXO(utxo);
        }

        // and add UTXOs corresponding to accepted transactions
        for (Transaction tx : acceptedTx) {
            for (int i = 0; i < tx.getOutputs().size(); i++) {
                UTXO utxo = new UTXO(tx.getHash(), i);
                Transaction.Output txOutput = tx.getOutput(i);
                utxoPool.addUTXO(utxo, txOutput);
            }
        }

        return acceptedTx;
    }

}
