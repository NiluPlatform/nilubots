package tech.nilu.bots.proxy.web3.contracts;

import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

public interface ERC20Basic {

    RemoteCall<String> name();

    RemoteCall<BigInteger> totalSupply();

    RemoteCall<BigInteger> rate();

    RemoteCall<BigInteger> decimals();

    RemoteCall<String> symbol();

    RemoteCall<Boolean> isPayable();

    RemoteCall<BigInteger> balanceOf(String me);

    RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value);
}
