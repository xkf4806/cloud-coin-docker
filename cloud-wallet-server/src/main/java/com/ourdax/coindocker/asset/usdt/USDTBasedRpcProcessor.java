package com.ourdax.coindocker.asset.usdt;

import com.google.common.collect.Lists;
import com.neemre.btcdcli4j.core.domain.Output;
import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.block.TransStatus;
import com.ourdax.coindocker.common.base.AssetException;
import com.ourdax.coindocker.common.base.RpcCallTemplate;
import com.ourdax.coindocker.common.clients.usdt.client.BtcdOmniClient;
import com.ourdax.coindocker.common.clients.usdt.omni.OmniTransaction;
import com.ourdax.coindocker.rpc.AbstractRpcProcessor;
import com.ourdax.coindocker.rpc.RpcBatchTransferRequest;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.web3j.abi.datatypes.Int;

/**
 * @author think on 9/2/2018
 */
public abstract class USDTBasedRpcProcessor extends AbstractRpcProcessor {

  private static final Integer MIN_CONFIRMATIONS = 6;

  private static final Integer MAX_CONFIRMATIONS = 99999999;

  protected abstract BtcdOmniClient getClient();

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
        new RpcCallTemplate<>(() -> {
          String blockHash = block.getBlockHash();

          if (StringUtils.isBlank(block.getBlockHash())) {
            blockHash = new RpcCallTemplate<>(
                () -> getClient().getBlockHash(Integer.parseInt(block.getBlockNumber()))).execute();
          }

          return getClient().getBlock(blockHash);
        }).execute();

    List<String> txIdList = new RpcCallTemplate<>(
        () -> getClient().omniListBlockTransactions(rpcBlock.getHeight().longValue()))
        .execute();
    List<TransInfo> transInfos = Lists.newArrayList();
    txIdList.stream().forEach(txId -> {
      List<TransInfo> usdtTrans = queryTransInfo(txId);
      if (!CollectionUtils.isEmpty(usdtTrans)) {
        transInfos.add(usdtTrans.get(0));
      }
    });

    BlockTrans result = new BlockTrans();
    result.setBlock(new SimpleBlock(rpcBlock.getHeight().toString(), rpcBlock.getHash()));
    result.setTrans(transInfos);
    return result;
  }

  @Override
  public List<TransInfo> queryTransInfo(String txId) {
    OmniTransaction transaction = new RpcCallTemplate<>(() -> getClient().getOmniTransaction(txId))
        .execute();
    return transaction.getPropertyId() == 31 ? Lists.newArrayList(adaptTransInfo(transaction))
        : Lists.newArrayList();

  }

  private TransInfo adaptTransInfo(OmniTransaction transaction) {
    TransInfo transInfo = new TransInfo();
    transInfo.setAmount(transaction.getAmount());
    transInfo.setTo(transaction.getReferenceAddress());
    transInfo.setBlockHash(transaction.getBlockHash());
    /**双花的问题解决方法*/
    transInfo.setConfirmNum(transaction.isValid() ? transaction.getConfirmations() : 0);
    transInfo.setFee(transaction.getFee());
    transInfo.setTxId(transaction.getTxId());
    return transInfo;
  }

  public BigDecimal queryBalance() {
    return new RpcCallTemplate<>(() -> getClient().omniGetBalance(getDepositAddress(), 31))
        .execute().getBalance();
  }

  public BigDecimal queryBalance(String inAddress) {
    return new RpcCallTemplate<>(() -> getClient().omniGetBalance(inAddress, 31))
        .execute().getBalance();
  }

  public BigDecimal queryChainMajorTokenBalance() {
    return new RpcCallTemplate<>(() -> {
      List<Output> outputs = getClient().listUnspent(MIN_CONFIRMATIONS, MAX_CONFIRMATIONS,
          Lists.newArrayList(getDepositAddress()));
      BigDecimal balance = BigDecimal.ZERO;
      for (Output op : outputs) {
        balance = balance.add(op.getAmount());
      }
      return balance;
    }).execute();
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
        () -> getClient()
            .omniSend(getDepositAddress(), request.getTo(), 31, request.getAmount()))
        .execute();
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

  @Override
  public String getUniformAccount() {
    return getDepositAddress();
  }

  @Override
  public RpcTransResponse transferFrom(String from, String to, BigDecimal amount) {
    String txId = new RpcCallTemplate<>(
        () -> getClient()
            .omniFundedSend(from, to, amount, getFeeAddress()))
        .execute();
    RpcTransResponse response = new RpcTransResponse();
    response.setTxId(txId);
    return response;
  }

  protected abstract String getFeeAddress();

  private void unlockWallet() {
//    new RpcCallTemplate<Void>(() -> {
//      getClient().walletPassphrase(getPassphrase(), DEFAULT_UNLOCK_TIMEOUT);
//      return null;
//    }).execute();
  }

  protected abstract String getDepositAddress();

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

    BigDecimal nextBlock = new BigDecimal(block.getBlockNumber()).add(BigDecimal.ONE);

    return queryTrans(new SimpleBlock(String.valueOf(nextBlock), null));
  }

  @Override
  public int getConfirmationNum(Block block) {
    return queryBCHBlock(block.getBlockHash()).getConfirmations();
  }

  @Override
  public BigDecimal queryChainMajorTokenBalance(String account) {
    return new RpcCallTemplate<>(() -> {
      List<Output> outputs = getClient()
          .listUnspent(MIN_CONFIRMATIONS, MAX_CONFIRMATIONS, Lists.newArrayList(account));
      BigDecimal balance = BigDecimal.ZERO;
      for (Output op : outputs) {
        balance = balance.add(op.getAmount());
      }
      return balance;
    }).execute();
  }

  protected BtcdOmniClient buildClient(String host, Integer port, String user, String password) {
    Properties clientProperties = new Properties();
    clientProperties.setProperty("node.bitcoind.rpc.protocol", "http");
    clientProperties.setProperty("node.bitcoind.rpc.host", host);
    clientProperties.setProperty("node.bitcoind.rpc.port", String.valueOf(port));
    clientProperties.setProperty("node.bitcoind.rpc.user", user);
    clientProperties.setProperty("node.bitcoind.rpc.password", password);
    clientProperties.setProperty("node.bitcoind.http.auth_scheme", "Basic");
    try {
      return new BtcdOmniClient(null, clientProperties);
    } catch (Exception e) {
      throw new RuntimeException("Init OmniClient error", e);
    }
  }

}
