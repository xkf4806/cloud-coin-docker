package com.ourdax.coindocker.asset.achain;

import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.block.TransStatus;
import com.ourdax.coindocker.common.base.AssetException;
import com.ourdax.coindocker.common.base.RpcCallTemplate;
import com.ourdax.coindocker.common.clients.achain.AchainClient;
import com.ourdax.coindocker.common.clients.achain.AchainClientException;
import com.ourdax.coindocker.common.clients.achain.pojo.AchainBlock;
import com.ourdax.coindocker.common.clients.achain.pojo.ActTransaction;
import com.ourdax.coindocker.common.enums.ACTContractEnum;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.rpc.AbstractRpcProcessor;
import com.ourdax.coindocker.rpc.RpcProcessor;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import com.ourdax.coindocker.utils.ACTUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author think on 30/1/2018
 */
@Slf4j
public abstract class AchainBasedRpcProcessor extends AbstractRpcProcessor implements RpcProcessor {

  private static final long DEFAULT_UNLOCK_TIMEOUT = 99999999L;

  private static final Integer ACT_DECIMAL = 5;

  @Autowired
  protected AchainClient achainClient;

  @Value("${achain.confirmThreshold}")
  private Integer confirmThreshold;

  private Integer getConfirmThreshold() {
    return confirmThreshold;
  }

  @Override
  public Block getLatestBlock() {
    Long latestBlockNumber = getLatestBlockNumber();
    AchainBlock achainBlock = getByBlockNumber(latestBlockNumber);
    return new SimpleBlock(String.valueOf(latestBlockNumber), achainBlock.getId());
  }

  public AchainBlock getByBlockNumber(Long blockNumber) {
    return new RpcCallTemplate<>(
        () -> achainClient.getBlockInfo(blockNumber)).execute();
  }

  protected Long getLatestBlockNumber() {
    return new RpcCallTemplate<>(
        () -> achainClient.getBlockCount()).execute();
  }

  @Override
  public BlockTrans queryTrans(Block block) {

    log.info("Querying trans for {}, block number: {}", getAssetCode(), block.getBlockNumber());
    List<ActTransaction> transactions = new RpcCallTemplate<>(() ->
        achainClient.getTransactions(Long.parseLong(block.getBlockNumber()))
    ).execute();

    return buildBlockTrans(transactions, block);

  }

  private BlockTrans buildBlockTrans(List<ActTransaction> transactions, Block block) {
    String blockHash = getByBlockNumber(Long.parseLong(block.getBlockNumber())).getId();
    block = new SimpleBlock(block.getBlockNumber(), blockHash);
    List<TransInfo> transInfoList = transactions.stream()
        .filter(transaction -> {
          if (AssetCode.ACT.equals(getAssetCode()) && StringUtils
              .isBlank(transaction.getContractId())) {
            return true;
          }
          if (!AssetCode.ACT.equals(getAssetCode()) && StringUtils
              .isNotBlank(transaction.getContractId()) && ACTContractEnum
              .containContract(transaction.getContractId()) &&
              StringUtils.equals("transfer_to_success", transaction.getEventType())) {
            return true;
          }
          return false;
        })
        .map(this::adaptTransInfo)
        .collect(Collectors.toList());

    BlockTrans blockTrans = new BlockTrans();
    blockTrans.setBlock(block);
    blockTrans.setTrans(transInfoList);
    return blockTrans;
  }

  public List<TransInfo> queryTransInfo(String txId) {

    ActTransaction transaction = new RpcCallTemplate<>(() -> achainClient.getTransaction(txId))
        .execute();
    return Optional.of(transaction).map(this::adaptTransInfo).map(Collections::singletonList)
        .orElseGet(Collections::emptyList);

  }

  protected TransInfo adaptTransInfo(ActTransaction transaction) {

    TransInfo transInfo = new TransInfo();
    transInfo.setTxId(transaction.getTrxId());
    transInfo.setBlockNumber(String.valueOf(transaction.getBlockNum()));
    transInfo.setBlockHash(getByBlockNumber(transaction.getBlockNum()).getId());

    /**act合约币在余额不足的情况下也会返回交易id，但是eventtype是失败的*/
    if (!StringUtils.equals("transfer_to_success", transaction.getEventType())) {
      transInfo.setConfirmNum(-1);
    }

    if (getAssetCode().isContract()) {
      ACTContractEnum code = ACTContractEnum.fromContract(transaction.getContractId());
      transInfo.setAssetCode(AssetCode.valueOf(code.name()));
    }

    transInfo.setFrom(transaction.getFromAddr());
    transInfo.setTo(StringUtils.isBlank(transaction.getSubAddress()) ? transaction.getToAddr()
        : transaction.getSubAddress());
    transInfo.setAmount(fromWei(BigInteger.valueOf(transaction.getAmount()), ACT_DECIMAL));
    transInfo.setFee(fromWei(BigInteger.valueOf(transaction.getFee()), ACT_DECIMAL));
    return transInfo;
  }


  private BigDecimal getBalanceByAccount(String accountName) {
    Long balance = new RpcCallTemplate<>(
        () -> achainClient.getBalance(accountName)).execute();
    return fromWei(BigInteger.valueOf(balance), ACT_DECIMAL);
  }

  @Override
  public TransStatus getTransStatus(TransInfo transInfo) {

    int confirmNum = getConfirmationNum(transInfo);
    transInfo.setConfirmNum(confirmNum);
    if (confirmNum > getConfirmThreshold()) {
      return TransStatus.COMPLETED;
    } else {
      return TransStatus.CONFIRMING;
    }

  }

  @Override
  public RpcTransResponse defaultTransfer(RpcTransRequest request) {
    log.info("Sending {} transfer transaction, to:{}, amount:{}",
        request.getAssetCode(), request.getTo(), request.getAmount());

    // do transfer
    String txId = doTransfer(request);

    RpcTransResponse response = new RpcTransResponse();
    response.setTxId(txId);
    log.info("{} transfer to {} successfully, txId: {}", getAssetCode(), request.getTo(), txId);
    return response;

  }

  @Override
  public BigDecimal queryBalance(String account) {
    throw new UnsupportedOperationException();
  }

  @Override
  public RpcTransResponse transferFrom(String from, String to, BigDecimal amount) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void preTransfer(RpcTransRequest rpcTransRequest) {
    openAndUnlock(); // wallet should be opened and unlocked before query balance & transfer.
    ensureFundSufficient(rpcTransRequest);
  }

  private void openAndUnlock() {
    try {
      log.info("open walletName : {}", getWalletName());
      achainClient.openWallet(getWalletName());
      log.info("unlock walletName : {}", getWalletName());
      achainClient.unlockWallet(DEFAULT_UNLOCK_TIMEOUT, getWalletPassword());
    } catch (AchainClientException e) {
      throw new AssetException("unlock walletName " + getWalletName() + " failed", e);
    }
  }

  protected abstract String doTransfer(RpcTransRequest request);

  protected abstract void ensureFundSufficient(RpcTransRequest rpcTransRequest);

  protected abstract String getAccountName();

  protected abstract String getWalletName();

  protected abstract String getWalletPassword();

  protected BigDecimal fromWei(BigInteger bigInteger, Integer decimal) {
    return ACTUtils.fromWei(bigInteger, decimal);
  }

  @Override
  public BigDecimal queryBalance() {
    return getBalanceByAccount(getAccountName());
  }

}
