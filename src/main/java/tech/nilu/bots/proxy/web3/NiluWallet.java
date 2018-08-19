package tech.nilu.bots.proxy.web3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import tech.nilu.bots.ActionCodes;
import tech.nilu.bots.exception.ApplicationException;
import tech.nilu.bots.proxy.web3.contracts.ERC20BasicImpl;

import java.math.BigDecimal;
import java.math.BigInteger;

@Component
public class NiluWallet {

    private Web3j web3j;

    public NiluWallet(@Autowired Web3j web3j) {
        this.web3j = web3j;
    }


    protected synchronized String giveNilu(Credentials credential, String address, BigDecimal value, String data) {
        try {
            BigInteger gasPrice = getGasPrice();
            BigInteger nonce = getNonce(credential);

            Transaction transaction = new Transaction(credential.getAddress(),
                    nonce
                    , gasPrice, null
                    , address
                    , BigInteger.ZERO
                    , data);
            BigInteger limit = estimateLimit(transaction);

            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce
                    , gasPrice
                    , limit.multiply(BigInteger.valueOf(1))
                    , address
                    , Convert.toWei(value, Convert.Unit.ETHER).toBigInteger(), data);
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credential);
            return web3j.ethSendRawTransaction(Numeric.toHexString(signedMessage)).send().getTransactionHash();

        } catch (Exception e) {
            throw new ApplicationException(500, ActionCodes.WEB3J_ERROR, e);
        }

    }

    public String giveToken(Credentials credential, String tokenAddress, String address, BigInteger value) {
        ERC20BasicImpl nilushell = ERC20BasicImpl.load(
                tokenAddress
                , web3j
                , credential
                , null
                , null);
        return giveNilu(credential, tokenAddress, new BigDecimal(0)
                , nilushell.prepareTransferData(address, value));
    }


    public String giveNilu(String mnemonic, String address, BigInteger value) {
        try {
            Credentials credential = credential(mnemonic);
            return Transfer.sendFunds(web3j, credential, address, Convert.fromWei(new BigDecimal(value), Convert.Unit.ETHER)
                    , Convert.Unit.ETHER).send().getTransactionHash();
        } catch (Exception e) {
            throw new ApplicationException(500, ActionCodes.WEB3J_ERROR, e);
        }
    }

    public String giveToken(String mnemonic, String tokenAddress, String address, BigInteger value) {
        try {
            Credentials credential = credential(mnemonic);
            ERC20BasicImpl nilushell = ERC20BasicImpl.load(
                    tokenAddress
                    , web3j
                    , credential
                    , getGasPrice()
                    , new BigInteger("4712388"));
            return nilushell.transfer(address, value).send().getTransactionHash();
        } catch (Exception e) {
            throw new ApplicationException(500, ActionCodes.WEB3J_ERROR, e);
        }
    }




    public double getBalance(String address) {
        try {
            return Convert.fromWei(
                    new BigDecimal(web3j.ethGetBalance(address
                            , DefaultBlockParameterName.LATEST).send().getBalance())
                    , Convert.Unit.ETHER).doubleValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BigInteger getTokenBalance(String tokenAddress, String address) {
        try {
            ERC20BasicImpl nilushell = ERC20BasicImpl.load(
                    tokenAddress
                    , web3j
                    , new ReadonlyTransactionManager(web3j, address)
                    , null
                    , null);
            return nilushell.balanceOf(address).send();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String[] generateWallet() {
        Bip44Wallet wallet = new Bip44Wallet(null);
        Bip44Wallet.HDKey key = wallet.createKey("M/44H/60H/0H/0/0");
        Credentials credentials = Credentials.create(key.priv.toString(16));
        return new String[]{credentials.getAddress(), wallet.getMnemonic()};
    }

    protected BigInteger getGasPrice() throws Exception {
        return web3j.ethGasPrice().send().getGasPrice();
    }

    protected BigInteger getNonce(Credentials credential) throws Exception {
        return web3j.ethGetTransactionCount(
                credential.getAddress(), DefaultBlockParameterName.LATEST)
                .send().getTransactionCount();
    }

    protected BigInteger estimateLimit(Transaction transaction) throws Exception {
        return web3j.ethEstimateGas(transaction).send().getAmountUsed();
    }

    protected Credentials credential(String mnemonic) {
        Bip44Wallet wallet = new Bip44Wallet((String) null, mnemonic);
        Bip44Wallet.HDKey key = wallet.createKey("M/44H/60H/0H/0/0");
        return Credentials.create(key.priv.toString(16));
    }
}
