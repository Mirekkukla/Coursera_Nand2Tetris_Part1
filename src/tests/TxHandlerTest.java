import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TxHandlerTest {

    private final KeyPair pk_scrooge; // Scrooge's Key
    private final KeyPair pk_alice; // Alice's Key

    private final Main.Tx rootTx;
    private final Main.Tx tx2;

    TxHandlerTest() throws NoSuchAlgorithmException {
        pk_scrooge = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        pk_alice = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        rootTx = createRootTx();
        tx2 = createTx2();
    }

    // single transaction: 10 (scrooge) -> 5, 3, 2 (alice)
    @Test
    void tx2isCorrect() {
        TxHandler handler = getHandlerWithRootTx();
        testHandling(handler, new Main.Tx[]{tx2}, new Main.Tx[]{tx2});
    }

    // single transaction
    @Test
    void allTx3sAreCorrectInIsolation() {
        // 3_1: 5 (alice) -> 5 (scrooge)
        TxHandler handler = getHandlerAfterTx2();
        Main.Tx tx3_1 = getTx3_1();
        testHandling(handler, new Main.Tx[]{tx3_1}, new Main.Tx[]{tx3_1});

        // 3_2: 5,3 (alice) -> 8 scrooge
        handler = getHandlerAfterTx2();  // reset
        Main.Tx tx3_2 = getTx3_2();
        testHandling(handler, new Main.Tx[]{tx3_2}, new Main.Tx[]{tx3_2});

        // 3_3: 2,3 (alice) -> 1,4 (scrooge)
        handler = getHandlerAfterTx2();  // reset
        Main.Tx tx3_3 = getTx3_3();
        testHandling(handler, new Main.Tx[]{tx3_3}, new Main.Tx[]{tx3_3});
    }

    @Test
    void testTxThatDependsOnOtherTxIsCorrectInIsolation() {
        TxHandler handler = getHandlerAfterTx2();
        Main.Tx tx3_3 = getTx3_3();
        handler.handleTxs(new Main.Tx[]{tx3_3}); // make sure tx3_3 is in the pool, as tx3_4 depends on it

        // 3_4: 4 (scrooge from second part of 3_3 transaction) -> 2,2 (scrooge)
        Main.Tx tx3_4 = getTx3_4(tx3_3);
        testHandling(handler, new Main.Tx[]{tx3_4}, new Main.Tx[]{tx3_4});
    }

    // both tx double-spend the same 5 coin, only the first should validate
    @Test
    void testSimpleDoubleSpending() {
        TxHandler handler = getHandlerAfterTx2();
        Main.Tx tx3_1 = getTx3_1();
        Main.Tx tx3_2 = getTx3_2();
        Main.Tx[] txsToHandle = {tx3_1, tx3_2};
        Main.Tx[] expectedTxs = {tx3_1};
        testHandling(handler, txsToHandle, expectedTxs);
    }

    // tx3_1 and tx3_2 in compatible
    @Test
    void testTwoLegalTx() {
        TxHandler handler = getHandlerAfterTx2();
        Main.Tx tx3_1 = getTx3_1();
        Main.Tx tx3_3 = getTx3_3();
        Main.Tx[] txsToHandle = {tx3_1, tx3_3};
        Main.Tx[] expectedTxs = {tx3_1, tx3_3};
        testHandling(handler, txsToHandle, expectedTxs);
    }

    @Test
    void testAllThreeTx() {
        TxHandler handler = getHandlerAfterTx2();
        Main.Tx tx3_1 = getTx3_1();
        Main.Tx tx3_2 = getTx3_2();
        Main.Tx tx3_3 = getTx3_3();
        Main.Tx[] txsToHandle = {tx3_1, tx3_2, tx3_3};
        Main.Tx[] expectedTxs = {tx3_1, tx3_3};
        testHandling(handler, txsToHandle, expectedTxs);
    }

    // tx3_4 depends on tx3_3; make sure we can process both in the same block
    @Test void testTxDependencyWithinBlock() {
        TxHandler handler = getHandlerAfterTx2();
        // 3_3: 2,3 (alice) -> 1,4 (scrooge)
        Main.Tx tx3_3 = getTx3_3();
        // 3_4: 4 (scrooge from second part of 3_3 transaction) -> 2,2 (scrooge)
        Main.Tx tx3_4 = getTx3_4(tx3_3);
        testHandling(handler, new Main.Tx[]{tx3_3, tx3_4}, new Main.Tx[]{tx3_3, tx3_4});
    }

    // CONTRUCTOR HELPERS

    private Main.Tx createRootTx() {
        Main.Tx rootTx = new Main.Tx();
        byte[] initialHash = BigInteger.valueOf(0).toByteArray(); // dummy value to avoid NPE
        rootTx.addInput(initialHash, 0);

        rootTx.addOutput(10, pk_scrooge.getPublic());
        safeSign(rootTx, pk_scrooge.getPrivate(), 0);
        return rootTx;
    }

    // scrooge's initial coin of 10 is converted in alice coins of 5, 3, and 2
    private Main.Tx createTx2() {
        Main.Tx tx2 = new Main.Tx();
        tx2.addInput(rootTx.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublic());
        tx2.addOutput(3, pk_alice.getPublic());
        tx2.addOutput(2, pk_alice.getPublic());
        safeSign(tx2, pk_scrooge.getPrivate(), 0);
        return tx2;
    }

    // TRANSACTIONS

    // give scrooge the 5 coin
    private Main.Tx getTx3_1() {
        Main.Tx tx3_1 = new Main.Tx();
        tx3_1.addInput(tx2.getHash(), 0); // $5 coin is at index 0
        tx3_1.addOutput(5, pk_scrooge.getPublic()); // give it back to scrooge
        safeSign(tx3_1, pk_alice.getPrivate(), 0);  // signed by alice
        return tx3_1;
    }

    // give scrooge the 5 and 3 coins
    private Main.Tx getTx3_2() {
        Main.Tx tx3_2 = new Main.Tx();
        tx3_2.addInput(tx2.getHash(), 0); // $5 coin
        tx3_2.addInput(tx2.getHash(), 1); // $3 coin
        tx3_2.addOutput(8, pk_scrooge.getPublic()); // give it back to scrooge
        safeSign(tx3_2, pk_alice.getPrivate(), 0); // signing the $5 output from before at index 0
        safeSign(tx3_2, pk_alice.getPrivate(), 1); // signing the $3 output from before at index 1
        return tx3_2;
    }

    // give scrooge the 3 and 2 coins in the form of a 1 and 4 coin
    private Main.Tx getTx3_3() {
        Main.Tx tx3_3 = new Main.Tx();
        tx3_3.addInput(tx2.getHash(), 1); // $3 coin
        tx3_3.addInput(tx2.getHash(), 2); // $2 coin
        tx3_3.addOutput(1, pk_scrooge.getPublic()); // give $1 back to scrooge
        tx3_3.addOutput(4, pk_scrooge.getPublic()); // give $4 back to scrooge

        // signing the $3 and $2 outputs from before, where they were at indices 1 and 2
        // GOTCHA: they're an _input_, and thus at indices 0 and 1
        safeSign(tx3_3, pk_alice.getPrivate(), 0);
        safeSign(tx3_3, pk_alice.getPrivate(), 1); // signing the $2 output from before
        return tx3_3;
    }

    // references tx_3_3: scrooge gives the 4 coin he just got back to alice in the form of two 2 coins
    private Main.Tx getTx3_4(Main.Tx tx3_3) {
        Main.Tx tx3_4 = new Main.Tx();
        tx3_4.addInput(tx3_3.getHash(), 1);
        tx3_4.addOutput(2, pk_alice.getPublic());
        tx3_4.addOutput(2, pk_alice.getPublic());

        safeSign(tx3_4, pk_scrooge.getPrivate(), 0);
        return tx3_4;
    }

    // OTHER HELPERS

    private TxHandler getHandlerWithRootTx() {
        UTXOPool pool = new UTXOPool();
        UTXO utxo = new UTXO(rootTx.getHash(),0);
        pool.addUTXO(utxo, rootTx.getOutput(0));
        return new TxHandler(pool);
    }

    private TxHandler getHandlerAfterTx2() {
        TxHandler handler = getHandlerWithRootTx();
        Main.Tx[] txToHandle = {tx2};
        handler.handleTxs(txToHandle);
        return handler;
    }

    private void testHandling(TxHandler handler, Main.Tx[] txsToHandle, Main.Tx[] expectedTxs) {
        Transaction[] validatedTxs = handler.handleTxs(txsToHandle);
        assertTrue(validatedTxs.length == expectedTxs.length);
        assertArrayEquals(expectedTxs, validatedTxs);
    }

    private void safeSign(Main.Tx tx, PrivateKey key, Integer input) {
        try {
            tx.signTx(key, input);
        } catch (SignatureException e) {
            throw new RuntimeException("Security fail");
        }
    }

}