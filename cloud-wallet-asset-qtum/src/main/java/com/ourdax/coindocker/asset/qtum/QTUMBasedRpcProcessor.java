package com.ourdax.coindocker.asset.qtum;

import com.neemre.btcdcli4j.core.domain.Payment;
import com.neemre.btcdcli4j.core.domain.PaymentOverview;
import com.neemre.btcdcli4j.core.domain.SinceBlock;
import com.neemre.btcdcli4j.core.domain.Transaction;
import com.neemre.btcdcli4j.core.domain.enums.PaymentCategories;
import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.block.TransInfo.Direction;
import com.ourdax.coindocker.block.TransStatus;
import com.ourdax.coindocker.common.base.AssetException;
import com.ourdax.coindocker.common.base.RpcCallTemplate;
import com.ourdax.coindocker.common.clients.qtum.QtumClient;
import com.ourdax.coindocker.rpc.AbstractRpcProcessor;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author think on 9/2/2018
 */
public abstract class QTUMBasedRpcProcessor extends AbstractRpcProcessor {

  private static final int INIT_CONFIRM_NUM = 1;

  @Autowired
  protected QtumClient client;

  protected abstract QtumClient getQtumClient();

  @Override
  public Block getLatestBlock() {
    String blockHash = new RpcCallTemplate<>(() -> getQtumClient().getBestBlockHash()).execute();
    return queryBlockByHash(blockHash);
  }

  private com.neemre.btcdcli4j.core.domain.Block queryQtumBlock(String blockHash) {
    return new RpcCallTemplate<>(() -> getQtumClient().getBlock(blockHash)).execute();
  }

  private Block queryBlockByHash(String blockHash) {
    return new SimpleBlock(
        String.valueOf(queryQtumBlock(blockHash).getHeight()),
        blockHash
    );
  }

  @Override
  public List<TransInfo> queryTransInfo(String txId) {

    Transaction transaction = new RpcCallTemplate<>(() -> getQtumClient().getTransaction(txId, true)).execute();
    return transaction.getDetails().stream()
        .map(payment -> adaptTransInfo(null, payment, transaction))
        .collect(Collectors.toList());

  }

  @Override
  public BlockTrans queryTrans(Block block) {
    throw new UnsupportedOperationException();
  }

  private TransInfo adaptTransInfo(Payment payment, PaymentOverview paymentOverview, Transaction transaction) {

    TransInfo transInfo = new TransInfo();
    if (payment == null){
      transInfo.setAmount(paymentOverview.getAmount().abs());
      transInfo.setFee(paymentOverview.getFee());
      transInfo.setTo(paymentOverview.getAddress());
      transInfo.setBlockHash(transaction.getBlockHash());
      transInfo.setConfirmNum(transaction.getConfirmations());
      transInfo.setTxId(transaction.getTxId());
      transInfo.setVout(String.valueOf(paymentOverview.getVOut()));
      transInfo.setDirection(getDirection(paymentOverview));
    } else {
      transInfo.setAmount(payment.getAmount());
      transInfo.setFee(payment.getFee());
      transInfo.setTo(payment.getAddress());
      transInfo.setBlockHash(payment.getBlockHash());
      transInfo.setConfirmNum(payment.getConfirmations());
      transInfo.setTxId(payment.getTxId());
      transInfo.setVout(String.valueOf(payment.getVOut()));
    }

    return transInfo;
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
    return new RpcCallTemplate<>(() -> getQtumClient().getBalance())
        .execute();
  }

  @Override
  public BigDecimal queryBalance(String account) {
    throw new UnsupportedOperationException();
  }

  protected abstract Integer getConfirmThreshold();

  @Override
  public TransStatus getTransStatus(TransInfo transInfo) {
    if (transInfo.getConfirmNum() == null){
      int confirmNum = getConfirmationNum(transInfo);
      transInfo.setConfirmNum(confirmNum);
    }
    if (transInfo.getConfirmNum() > getConfirmThreshold()) {
      return TransStatus.COMPLETED;
    } else {
      return TransStatus.CONFIRMING;
    }
  }

  @Override
  public RpcTransResponse defaultTransfer(RpcTransRequest request) {
    String txId = new RpcCallTemplate<>(
        () -> getQtumClient().sendToAddress(request.getTo(), request.getAmount())).execute();
    RpcTransResponse response = new RpcTransResponse();
    response.setTxId(txId);
    return response;
  }


  @Override
  public void preTransfer(RpcTransRequest rpcTransRequest) {
    ensureFundSufficient(queryBalance(), rpcTransRequest.getAmount());
  }

  private void ensureFundSufficient(BigDecimal balance, BigDecimal request) {
    if (balance.compareTo(request) < 0) {
      String msg = String.format("insufficient fund, %s remaining %s", getAssetCode(), balance);
      throw new AssetException(msg);
    }
  }

  @Override
  public BlockTrans queryTransSince(Block block) {
    com.neemre.btcdcli4j.core.domain.Block lastBlock =
        new RpcCallTemplate<>(() -> getQtumClient().getBlock(block.getBlockHash())).execute();
    if (lastBlock == null) {
      return null;
    }

    SinceBlock sinceBlock = new RpcCallTemplate<>(() ->
        getQtumClient().listSinceBlock(block.getBlockHash(), INIT_CONFIRM_NUM, true)).execute();
    List<TransInfo> transInfos = sinceBlock.getPayments().stream()
        .filter(this::isTransferInType)
        .map(payment -> adaptTransInfo(payment, null, null))
        .collect(Collectors.toList());

    BlockTrans result = new BlockTrans();
    result.setBlock(queryBlockByHash(sinceBlock.getLastBlock()));
    result.setTrans(transInfos);
    return result;
  }

  @Override
  public int getConfirmationNum(Block block) {
    return queryQtumBlock(block.getBlockHash()).getConfirmations();
  }

  @Override
  public RpcTransResponse transferFrom(String from, String to, BigDecimal amount) {
    return null;
  }

  private boolean isTransferInType(PaymentOverview payment) {
    return payment.getCategory() == PaymentCategories.RECEIVE;
  }


}
