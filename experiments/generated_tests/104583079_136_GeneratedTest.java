java
package org.bitcoinj.core;

import org.bitcoinj.script.ScriptBuilder;
import org.junit.Test;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionTest {

    @Test
    public void testCheckCoinBaseHeightValid() throws VerificationException {
        Transaction transaction = new Transaction(NetworkParameters.ID_MAINNET);
        TransactionInput input = new TransactionInput(NetworkParameters.ID_MAINNET, transaction, new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
        transaction.addInput(input);
        int height = 1;
        transaction.checkCoinBaseHeight(height);
    }

    @Test
    public void testCheckCoinBaseHeightInvalidHeight() {
        Transaction transaction = new Transaction(NetworkParameters.ID_MAINNET);
        assertThrows(IllegalArgumentException.class, () -> transaction.checkCoinBaseHeight(Block.BLOCK_HEIGHT_GENESIS - 1));
    }

    @Test
    public void testCheckCoinBaseHeightNotCoinBase() {
        Transaction transaction = new Transaction(NetworkParameters.ID_MAINNET);
        assertThrows(IllegalStateException.class, () -> transaction.checkCoinBaseHeight(1));
    }

    @Test
    public void testCheckCoinBaseHeightMismatch() {
        Transaction transaction = new Transaction(NetworkParameters.ID_MAINNET);
        TransactionInput input = new TransactionInput(NetworkParameters.ID_MAINNET, transaction, new byte[]{(byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
        transaction.addInput(input);
        int height = 1;
        assertThrows(VerificationException.CoinbaseHeightMismatch.class, () -> transaction.checkCoinBaseHeight(height));
    }

    @Test
    public void testCheckCoinBaseHeightMismatchLength() {
        Transaction transaction = new Transaction(NetworkParameters.ID_MAINNET);
        TransactionInput input = new TransactionInput(NetworkParameters.ID_MAINNET, transaction, new byte[]{(byte) 0x01});
        transaction.addInput(input);
        int height = 1000;
        assertThrows(VerificationException.CoinbaseHeightMismatch.class, () -> transaction.checkCoinBaseHeight(height));
    }

    @Test
    public void testCheckCoinBaseHeightMismatchedData() {
        Transaction transaction = new Transaction(NetworkParameters.ID_MAINNET);
        TransactionInput input = new TransactionInput(NetworkParameters.ID_MAINNET, transaction, new byte[]{(byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
        transaction.addInput(input);
        int height = 1;
        assertThrows(VerificationException.CoinbaseHeightMismatch.class, () -> transaction.checkCoinBaseHeight(height));
    }
}