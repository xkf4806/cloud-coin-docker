package com.ourdax.coindocker.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

/**
 * Created by zhangjinyang on 2018/8/2.
 */

public class ERC20Contract extends Contract{

  protected ERC20Contract(String contractBinary, String contractAddress,
      Web3j web3j,
      TransactionManager transactionManager, BigInteger gasPrice,
      BigInteger gasLimit) {
    super(contractBinary, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
  }

  public static ERC20Contract load(String contractBinary, String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
    return new ERC20Contract(contractBinary, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
  }


  public RemoteCall<TransactionReceipt> transferFrom(String from, String to, BigInteger amount) {
    Function function = new Function(
        "transferFrom",
        Arrays.<Type>asList(new Address(from),
            new Address(to),
            new Uint256(amount)),
        Collections.<TypeReference<?>>emptyList());
    return executeRemoteCallTransaction(function);
  }

  public RemoteCall<TransactionReceipt> transfer(String to, BigInteger amount) {
    Function function = new Function(
        "transfer",
        Arrays.<Type>asList(new Address(to),
            new Uint256(amount)),
        Collections.<TypeReference<?>>emptyList());
    return executeRemoteCallTransaction(function);
  }

  public RemoteCall<BigInteger> balanceOf(String _owner) {
    Function function = new Function("balanceOf",
        Arrays.<Type>asList(new Address(_owner)),
        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    return executeRemoteCallSingleValueReturn(function, BigInteger.class);
  }


}
