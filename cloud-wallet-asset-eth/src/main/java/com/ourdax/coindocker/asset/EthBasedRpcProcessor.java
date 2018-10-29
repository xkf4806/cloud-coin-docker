package com.ourdax.coindocker.asset;

import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.block.TransStatus;
import com.ourdax.coindocker.common.base.AssetException;
import com.ourdax.coindocker.common.base.RpcCallTemplate;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.rpc.AbstractRpcProcessor;
import com.ourdax.coindocker.rpc.RpcProcessor;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import com.ourdax.coindocker.utils.EthUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * @author think on 25/1/2018
 */
@Slf4j
public abstract class EthBasedRpcProcessor extends AbstractRpcProcessor implements RpcProcessor {

  protected static final BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L);

  protected static final BigInteger GAS_LIMIT = BigInteger.valueOf(210000);

  protected static final String TX_SUCCESS_STATUS = "0x1";

  protected static final int ETH_DECIMALS = 18;

  protected static final int ETH_CONFIRM_NUM = 30;


  protected abstract Admin getClient();

  protected abstract String getDepositAddress();

  @Override
  public Block getLatestBlock() {
    BigInteger latestBlockNumber = getLatestBlockNumber();
    EthBlock ethBlock = getEthBlock(latestBlockNumber);
    return new SimpleBlock(String.valueOf(latestBlockNumber), ethBlock.getBlock().getHash());
  }

  private EthBlock getEthBlock(BigInteger blockNumber) {
    return getByBlockNumber(blockNumber, false);
  }

  protected EthBlock getDetailBlock(BigInteger blockNumber) {
    return getByBlockNumber(blockNumber, true);
  }

  private EthBlock getByBlockNumber(BigInteger blockNumber, boolean returnFullTrans) {
    return new RpcCallTemplate<>(
        () -> getClient()
            .ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), returnFullTrans)
            .send()).execute();
  }

  protected BigInteger getLatestBlockNumber() {
    return new RpcCallTemplate<>(
        () -> getClient().ethBlockNumber().send()).execute()
        .getBlockNumber();
  }

  @Override
  public BlockTrans queryTrans(Block block) {
    log.info("Querying trans for {}, block number: {}", getAssetCode(), block.getBlockNumber());
    BigInteger blockNumber = new BigInteger(block.getBlockNumber(), 10);
    EthBlock ethBlock = getDetailBlock(blockNumber);
    return buildBlockTrans(ethBlock);
  }


  public List<TransInfo> queryTransInfo(String txId) {
    Optional<TransInfo> transInfo = new RpcCallTemplate<>(
        () -> getClient().ethGetTransactionByHash(txId).send().getTransaction()).execute()
        .map(this::adaptTransInfo);
    return transInfo.map(Collections::singletonList).orElseGet(Collections::emptyList);
  }

  protected BigDecimal getEthBalance(String address) {
    BigInteger balance = new RpcCallTemplate<>(
        () -> getClient().ethGetBalance(
            address, DefaultBlockParameter.valueOf(getLatestBlockNumber())).send()).execute()
        .getBalance();

    return EthUtils.fromWei(balance, ETH_DECIMALS);
  }

  @Override
  public TransStatus getTransStatus(TransInfo transInfo) {
    int confirmNum = getConfirmationNum(transInfo);
    transInfo.setConfirmNum(confirmNum);
    if (getConfirmationNum(transInfo) > getConfirmThreshold()) {
      return TransStatus.COMPLETED;
    } else {
      return TransStatus.CONFIRMING;
    }
  }

  protected abstract int getConfirmThreshold();


  @Override
  public RpcTransResponse defaultTransfer(RpcTransRequest request) {
    log.info("Sending {} transfer out transaction to:{}, amount:{}",
        getAssetCode(), request.getTo(), request.getAmount());

    // do transfer
    String txId = doTransfer(request.getTo(), request.getAmount());
    if (StringUtils.isEmpty(txId)) {
      throw new AssetException("Transfer out got empty txId");
    }

    RpcTransResponse response = new RpcTransResponse();
    response.setTxId(txId);
    log.info("{} transfer to {} successfully, txId: {}", getAssetCode(), request.getTo(), txId);
    return response;
  }

  @Override
  public String getUniformAccount() {
    return getDepositAddress();
  }

  @Override
  public RpcTransResponse transferFrom(String from, String to, BigDecimal amount) {

    log.info("Sending {} transfer out transaction from:{}, to:{}, amount:{}",
        getAssetCode(), from, to, amount);

    String txId = new RpcCallTemplate<>(() -> {
      EthSendTransaction transaction = getClient()
          .ethSendTransaction(org.web3j.protocol.core.methods.request.Transaction
              .createEtherTransaction(from, null, GAS_PRICE,
                  GAS_LIMIT, to, EthUtils.toWei(amount, ETH_DECIMALS)))
          .send();
      return transaction.getTransactionHash();
    }).execute();

    if (StringUtils.isEmpty(txId)) {
      throw new AssetException("Transfer out got empty txId");
    }

    RpcTransResponse response = new RpcTransResponse();
    response.setTxId(txId);
    log.info("{} transfer from {} to {} successfully, txId: {}", getAssetCode(),
        from, to, txId);
    return response;

  }

  @Override
  public BigDecimal queryChainMajorTokenBalance() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BigDecimal queryChainMajorTokenBalance(String inAddress) {
    throw new UnsupportedOperationException();
  }

  protected void ensureFundSufficient(BigDecimal transferAmount) {
    BigDecimal balance = queryBalance();
    boolean sufficient = balance.compareTo(transferAmount) >= 0;
    if (!sufficient) {
      String msg = String.format("insufficient fund, %s remaining %s", getAssetCode(), balance);
      throw new AssetException(msg);
    }
  }

  protected void unlockAddress(String address, String passphrase) {
    log.info("unlockAddress " + getAssetCode().name() + "---- " + passphrase);
    PersonalUnlockAccount result = new RpcCallTemplate<>(
        () -> getClient().personalUnlockAccount(address, passphrase).send()).execute();
    boolean unlockSuccess = result.getResult() != null && result.getResult();
    if (unlockSuccess) {
      log.info("Unlock account successfully, address: {}", address);
    } else {
      throw new AssetException(String.format("Unlock account %s failed", address));
    }
  }

  protected BlockTrans buildBlockTrans(EthBlock ethBlock) {
    if (ethBlock.getBlock() == null) {
      return null;
    }

    SimpleBlock block = new SimpleBlock(
        String.valueOf(ethBlock.getBlock().getNumber()),
        ethBlock.getBlock().getHash()
    );

    List<TransInfo> transInfoList = ethBlock.getBlock().getTransactions().stream()
        .map(Transaction.class::cast)
        .filter(transaction -> {
          EthGetTransactionReceipt receipt = null;
          try {
            receipt = getClient()
                .ethGetTransactionReceipt(transaction.getHash()).send();
          } catch (IOException e) {
            log.error("query transaction receipt by hash failed. txHash={}, assetCode={}",
                transaction.getHash(), getAssetCode());
            return false;
          }

          if (!receipt.getResult().getStatus().equals(TX_SUCCESS_STATUS)) {
            return false;
          }
          return true;
        })
        .map(this::adaptTransInfo)
        .collect(Collectors.toList());

    BlockTrans blockTrans = new BlockTrans();
    blockTrans.setBlock(block);
    blockTrans.setTrans(transInfoList);
    return blockTrans;
  }

  protected TransInfo adaptTransInfo(Transaction transaction) {
    TransInfo transInfo = new TransInfo();
    transInfo.setTxId(transaction.getHash());
    transInfo.setBlockNumber(String.valueOf(transaction.getBlockNumber()));
    transInfo.setBlockHash(transaction.getBlockHash());
    transInfo.setFrom(transaction.getFrom());
    transInfo.setTo(transaction.getTo());
    transInfo.setAmount(parseValue(transaction.getValue()));
    transInfo.setFee(parseValue(transaction.getGas()));

    return transInfo;
  }

  protected abstract String doTransfer(String to, BigDecimal amount);

  private BigDecimal parseValue(BigInteger value) {

    double pow = Math.pow(10, 18);
    return new BigDecimal(value).divide(new BigDecimal(pow))
        .setScale(4, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();

  }

}
