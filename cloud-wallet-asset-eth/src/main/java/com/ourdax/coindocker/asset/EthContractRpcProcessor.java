package com.ourdax.coindocker.asset;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Predicates;
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.common.base.AssetException;
import com.ourdax.coindocker.common.base.RpcCallTemplate;
import com.ourdax.coindocker.contract.ERC20Contract;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import com.ourdax.coindocker.utils.EthUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

/**
 * @author think on 13/1/2018
 */
@Slf4j
public abstract class EthContractRpcProcessor extends EthBasedRpcProcessor {

  private static final String TRANSFER_TRAN_CODE = "0xa9059cbb";

  private static final BigDecimal ETH_REMAINING = new BigDecimal("0.01");

  private static final Cache<BigInteger, EthBlock> ethBlockCache = CacheBuilder.newBuilder()
      .expireAfterWrite(1, TimeUnit.MINUTES)
      .maximumSize(10)
      .build();

  protected abstract String getContractAddress();

  @Value("${eth.depositAddress}")
  protected String depositAddress;

  @Value("${eth.passphrase}")
  private String passphrase;

  protected ERC20Contract contract;

  protected String getDepositAddress() {
    return depositAddress;
  }

  @Override
  protected EthBlock getDetailBlock(BigInteger blockNumber) {
    try {
      return ethBlockCache.get(blockNumber, () -> super.getDetailBlock(blockNumber));
    } catch (ExecutionException e) {
      Throwables.throwIfUnchecked(e.getCause());
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public BlockTrans buildBlockTrans(EthBlock ethBlock) {
    if (ethBlock.getBlock() == null) {
      return null;
    }

    SimpleBlock block = new SimpleBlock(
        String.valueOf(ethBlock.getBlock().getNumber()),
        ethBlock.getBlock().getHash()
    );

    List<TransInfo> transInfoList = ethBlock.getBlock().getTransactions().stream()
        .map(Transaction.class::cast)
        .filter(
            transaction -> {
              if (!isTransferType(transaction.getInput()) ||
                  !StringUtils.equalsIgnoreCase(transaction.getTo(), getContractAddress())) {
                return false;
              }

              EthGetTransactionReceipt receipt = null;
              try {
                receipt = getClient()
                    .ethGetTransactionReceipt(transaction.getHash()).send();
              } catch (IOException e) {
                log.error("query transaction receipt by hash failed. txHash={}, assetCode={}",
                    transaction.getHash(), getAssetCode());
                return false;
              }

              if (!StringUtils.equalsIgnoreCase(receipt.getResult().getStatus(), TX_SUCCESS_STATUS)
                  ||
                  receipt.getResult().getLogs().isEmpty()) {
                return false;
              }
              return true;
            }).map(this::adaptTransInfo)
        .filter(Predicates.notNull())
        .collect(Collectors.toList());

    BlockTrans blockTrans = new BlockTrans();
    blockTrans.setBlock(block);
    blockTrans.setTrans(transInfoList);
    return blockTrans;
  }


  private String parseTransType(String input) {
    return (StringUtils.isNotEmpty(input) && input.length() < 10) ? null : input.substring(0, 10);
  }

  private boolean isTransferType(String input) {
    return StringUtils.equals(TRANSFER_TRAN_CODE, parseTransType(input));
  }

  private String parseToAddress(String input) {
    return input.length() < 74 ? null : "0x" + input.substring(34, 74);
  }

  private BigDecimal parseAmount(String input) {
    BigDecimal originAmount = new BigDecimal(new BigInteger(input.substring(74), 16));
    return originAmount.divide(EthUtils.getRatio(getDecimals()));
  }

  @Override
  public TransInfo adaptTransInfo(Transaction transaction) {
    try {
      TransInfo transInfo = new TransInfo();
      transInfo.setTxId(transaction.getHash());
      transInfo.setBlockNumber(String.valueOf(transaction.getBlockNumber()));
      transInfo.setBlockHash(transaction.getBlockHash());

      String input = transaction.getInput();
      transInfo.setFrom(transaction.getFrom());
      transInfo.setTo(parseToAddress(input));
      transInfo.setAmount(parseAmount(input));
      return transInfo;
    } catch (Exception e) {
      log.error("transaction info " + JSON.toJSONString(transaction), e);
      return null;
    }
  }

  @Override
  public BigDecimal queryChainMajorTokenBalance() {
    return getEthBalance(depositAddress);
  }

  @Override
  public BigDecimal queryChainMajorTokenBalance(String inAddress) {
    return getEthBalance(inAddress);
  }

  @Override
  public void preTransfer(RpcTransRequest rpcTransRequest) {
    ensureFundSufficient(rpcTransRequest.getAmount());
    unlockAddress(depositAddress, passphrase);
  }

  @Override
  public String getUniformAccount() {
    return depositAddress;
  }


  @Override
  public BigDecimal queryBalance() {
    BigInteger originValue = new RpcCallTemplate<>(
        () -> contract.balanceOf(getDepositAddress()).send()
    ).execute();
    return fromWei(originValue);
  }

  @Override
  public BigDecimal queryBalance(String account) {
    BigInteger originValue = new RpcCallTemplate<>(
        () -> contract.balanceOf(account).send()
    ).execute();

    return fromWei(originValue);
  }

  @Override
  public RpcTransResponse transferFrom(String from, String to, BigDecimal amount) {

    log.info("Sending {} transfer out transaction from:{}, to:{}, amount:{}",
        getAssetCode(), from, to, amount);
    String txId = new RpcCallTemplate<>(
        () -> {
          TransactionReceipt trans = contract
              .transferFrom(from, to,
                  toWei(amount)).send();
          return trans.getTransactionHash();
        }
    ).execute();
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
  protected String doTransfer(String to, BigDecimal amount) {
    return new RpcCallTemplate<>(
        () -> {
          TransactionReceipt trans = contract.transfer(to, toWei(amount)).send();
          return trans.getTransactionHash();
        }
    ).execute();
  }

  @Override
  protected void ensureFundSufficient(BigDecimal transferAmount) {
    BigDecimal ethBalance = queryChainMajorTokenBalance();
    BigDecimal assetBalance = queryBalance();
    boolean sufficient =
        ethBalance.compareTo(ETH_REMAINING) > 0 && assetBalance.compareTo(transferAmount) > 0;

    if (!sufficient) {
      String msg = String.format("insufficient fund, eth remaining %s, %s remaining %s",
          ethBalance, getAssetCode(), assetBalance);
      throw new AssetException(msg);
    }
  }

  protected abstract int getDecimals();

  protected BigInteger toWei(BigDecimal bigDecimal) {
    return EthUtils.toWei(bigDecimal, getDecimals());
  }

  protected BigDecimal fromWei(BigInteger bigInteger) {
    return EthUtils.fromWei(bigInteger, getDecimals());
  }

  @Override
  protected int getConfirmThreshold() {
    return ETH_CONFIRM_NUM;
  }
}
