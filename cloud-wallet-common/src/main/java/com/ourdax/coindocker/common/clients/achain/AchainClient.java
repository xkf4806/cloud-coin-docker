package com.ourdax.coindocker.common.clients.achain;

import com.ourdax.coindocker.common.clients.achain.pojo.AchainBlock;
import com.ourdax.coindocker.common.clients.achain.pojo.ActTransaction;
import com.ourdax.coindocker.common.clients.achain.pojo.ContractResult;
import com.ourdax.coindocker.common.clients.achain.pojo.ContractTransaction;
import com.ourdax.coindocker.common.clients.achain.pojo.PrettyTransaction;
import com.ourdax.coindocker.common.clients.achain.pojo.Transaction;
import com.ourdax.coindocker.common.clients.achain.pojo.WalletInfo;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author think on 31/1/2018
 */
public interface AchainClient {

  Long getBlockCount() throws AchainClientException;

  AchainBlock getBlockInfo(Long blockNumber) throws AchainClientException;

  Long getBalance(String accountName) throws AchainClientException;

  Transaction getWalletTransaction(String walletName, String txHash) throws AchainClientException;


  PrettyTransaction getPrettyTransaction(String txHash) throws AchainClientException;

  ContractResult getContractResult(String txHash) throws AchainClientException;

  ActTransaction getTransaction(String txHash) throws AchainClientException;

  List<ActTransaction> getTransactions(Long blockNum) throws AchainClientException;

  ActTransaction getContractTransaction(String txHash) throws AchainClientException;

  void openWallet(String wallet) throws AchainClientException;

  WalletInfo getWalletInfo(String wallet) throws AchainClientException;

  void unlockWallet(Long timeout, String password) throws AchainClientException;

  Transaction transferToAddress(String fromAccount, String toAddress, String assetSymbol,
      BigDecimal amount, String memo, String strategy)
          throws AchainClientException;

  Transaction transferToAddress(String fromAccount, String toAddress, String assetSymbol,
      BigDecimal amount)
              throws AchainClientException;

  ContractTransaction callContract(String contractId, String from, String method,
      String methodParam, String assetSymbol, BigDecimal ceilingFee)
                          throws AchainClientException;

  Long getContractBalance(String account, String contractId) throws AchainClientException;

  ContractTransaction transferToContract(String contractId, String fromAccount, String toAddress,
      String assetSymbol, BigDecimal amount, BigDecimal ceilingFee)
                              throws AchainClientException;

}
