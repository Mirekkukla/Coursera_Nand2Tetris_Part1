// Does it need to extend?
public class MaxFeeHandler extends TxHandler {

    public MaxFeeHandler(UTXOPool utxoPool) {
        super(utxoPool);
    }

    @Override
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        return null;
    }


}
