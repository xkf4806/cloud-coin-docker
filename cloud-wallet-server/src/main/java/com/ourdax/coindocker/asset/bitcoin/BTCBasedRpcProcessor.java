package com.ourdax.coindocker.asset.bitcoin;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.MoreExecutors;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import com.neemre.btcdcli4j.core.domain.PaymentOverview;
import com.neemre.btcdcli4j.core.domain.SinceBlock;
import com.neemre.btcdcli4j.core.domain.Transaction;
import com.neemre.btcdcli4j.core.domain.enums.PaymentCategories;
import com.ourdax.coindocker.block.*;
import com.ourdax.coindocker.block.TransInfo.Direction;
import com.ourdax.coindocker.common.base.AssetException;
import com.ourdax.coindocker.common.base.RpcCallTemplate;
import com.ourdax.coindocker.rpc.*;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author think on 9/2/2018
 */
public abstract class BTCBasedRpcProcessor extends AbstractRpcProcessor {

  private static final int INIT_CONFIRM_NUM = 1;

  protected abstract BtcdClient getClient();

  @Override
  public Block getLatestBlock() {
    String blockHash = new RpcCallTemplate<>(() -> getClient().getBestBlockHash()).execute();
    return queryBlockByHash(blockHash);
  }

  private com.neemre.btcdcli4j.core.domain.Block queryBCHBlock(String blockHash) {
    return new RpcCallTemplate<>(() -> getClient().getBlock(blockHash)).execute();
  }

  private Block queryBlockByHash(String blockHash) {
    return new SimpleBlock(
        String.valueOf(queryBCHBlock(blockHash).getHeight()),
        blockHash
    );
  }

  @Override
  public BlockTrans queryTrans(Block block) {
    com.neemre.btcdcli4j.core.domain.Block rpcBlock =
        new RpcCallTemplate<>(() -> getClient().getBlock(block.getBlockHash())).execute();

    List<CompletableFuture<List<TransInfo>>> completableFutures = rpcBlock.getTx().stream()
        .map(txId -> CompletableFuture
            .supplyAsync(() -> queryTransInfo(txId), MoreExecutors.directExecutor()))
        .collect(Collectors.toList());
    CompletableFuture<Void> futureResults = CompletableFuture.allOf(
        completableFutures.toArray(new CompletableFuture[0]));
    CompletableFuture<List<TransInfo>> transInfosFuture = futureResults.thenApply(v ->
        completableFutures.stream()
            .flatMap(future -> future.join().stream())
            .collect(Collectors.toList())
    );
    List<TransInfo> transInfos = transInfosFuture.join();

    BlockTrans result = new BlockTrans();
    result.setBlock(new SimpleBlock(String.valueOf(rpcBlock.getHeight()), block.getBlockHash()));
    result.setTrans(transInfos);
    return result;
  }

  @Override
  public List<TransInfo> queryTransInfo(String txId) {
    Transaction transaction = new RpcCallTemplate<>(() -> getClient().getTransaction(txId, true))
        .execute();
    return transaction.getDetails().stream()
        .map(payment -> adaptTransInfo(payment, transaction))
        .collect(Collectors.toList());
  }

  private boolean isTransferInType(PaymentOverview payment) {
    return payment.getCategory() == PaymentCategories.RECEIVE;
  }

  private Direction getDirection(PaymentOverview paymentOverview) {
    PaymentCategories category = paymentOverview.getCategory();
    if (category == PaymentCategories.SEND) {
      return Direction.OUT;
    }
    if (category == PaymentCategories.RECEIVE) {
      return Direction.IN;
    }
    return null;
  }

  @Override
  public BigDecimal queryBalance() {
    return new RpcCallTemplate<>(() -> getClient().getBalance(getDepositAccount())).execute();
  }

  /**btc系列不支持使用指定account查询 */
  @Override
  public BigDecimal queryBalance(String account) {
    throw new UnsupportedOperationException();
  }

  protected abstract Integer getConfirmThreshold();

  @Override
  public TransStatus getTransStatus(TransInfo transInfo) {
    if (transInfo.getConfirmNum() > getConfirmThreshold()) {
      return TransStatus.COMPLETED;
    } else {
      return TransStatus.CONFIRMING;
    }
  }

  @Override
  public RpcTransResponse defaultTransfer(RpcTransRequest request) {
    String txId = new RpcCallTemplate<>(
        () -> getClient().sendToAddress(request.getTo(), request.getAmount())).execute();
    RpcTransResponse response = new RpcTransResponse();
    response.setTxId(txId);
    return response;
  }

  @Override
  public void preTransfer(RpcTransRequest rpcTransRequest) {
    ensureFundSufficient(queryBalance(), rpcTransRequest.getAmount());
    unlockWallet();
  }

  @Override
  public void preBatchTransfer(RpcBatchTransferRequest request) {
    BigDecimal total = request.getBatchRequests().stream()
        .map(RpcTransRequest::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    ensureFundSufficient(queryBalance(), total);

    // todo
    unlockWallet();
  }

  private void unlockWallet() {
//    new RpcCallTemplate<Void>(() -> {
//      getClient().walletPassphrase(getPassphrase(), DEFAULT_UNLOCK_TIMEOUT);
//      return null;
//    }).execute();
  }

//  protected abstract String getPassphrase();


  @Override
  public RpcBatchTransferResponse batchTransfer(RpcBatchTransferRequest batchRequest) {
    List<RpcTransRequest> batchRequests = batchRequest.getBatchRequests();
    if (CollectionUtils.isEmpty(batchRequests)) {
      throw new RuntimeException("Batch transfer require at least one transfer record");
    }

    return new RpcCallTemplate<>(() -> {
      Map<String, BigDecimal> toAddressMap = Maps.newHashMap();
      batchRequest.getBatchRequests().forEach(
          request -> toAddressMap.put(request.getTo(), request.getAmount()));
      String txId = getClient().sendMany(getDepositAccount(), toAddressMap);
      RpcBatchTransferResponse response = new RpcBatchTransferResponse();
      response.setTxId(txId);
      return response;
    }).execute();
  }

  @Override
  public String getUniformAccount() {
    return getDepositAccount();
  }

  @Override
  public RpcTransResponse transferFrom(String from, String to, BigDecimal amount) {
    throw new UnsupportedOperationException();
  }

  protected abstract String getDepositAccount();

  private void ensureFundSufficient(BigDecimal balance, BigDecimal request) {
    if (balance.compareTo(request) < 0) {
      String msg = String.format("insufficient fund, %s remaining %s", getAssetCode(), balance);
      throw new AssetException(msg);
    }
  }

  @Override
  public BlockTrans queryTransSince(Block block) {
    com.neemre.btcdcli4j.core.domain.Block lastBlock =
        new RpcCallTemplate<>(() -> getClient().getBlock(block.getBlockHash())).execute();
    if (lastBlock == null) {
      return null;
    }

    SinceBlock sinceBlock = new RpcCallTemplate<>(() ->
        getClient().listSinceBlock(block.getBlockHash(), INIT_CONFIRM_NUM, true)).execute();
    List<TransInfo> transInfos = sinceBlock.getPayments().stream()
        .filter(this::isTransferInType)
        .map(this::adaptTransInfo)
        .collect(Collectors.toList());

    BlockTrans result = new BlockTrans();
    result.setBlock(queryBlockByHash(sinceBlock.getLastBlock()));
    result.setTrans(transInfos);
    return result;
  }

  @Override
  public int getConfirmationNum(Block block) {
    return queryBCHBlock(block.getBlockHash()).getConfirmations();
  }

  @Override
  public BigDecimal queryChainMajorTokenBalance() {
    throw new UnsupportedOperationException();
  }
  @Override
  public BigDecimal queryChainMajorTokenBalance(String inAddress) {
    throw new UnsupportedOperationException();
  }

  protected BtcdClient buildClient(String host, Integer port, String user, String password) {
    Properties clientProperties = new Properties();
    clientProperties.setProperty("node.bitcoind.rpc.protocol", "http");
    clientProperties.setProperty("node.bitcoind.rpc.host", host);
    clientProperties.setProperty("node.bitcoind.rpc.port", String.valueOf(port));
    clientProperties.setProperty("node.bitcoind.rpc.user", user);
    clientProperties.setProperty("node.bitcoind.rpc.password", password);
    clientProperties.setProperty("node.bitcoind.http.auth_scheme", "Basic");
    try {
      return new BtcdClientImpl(clientProperties);
    } catch (Exception e) {
      throw new RuntimeException("Init BtcdClient error", e);
    }
  }
}
