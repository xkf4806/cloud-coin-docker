package com.ourdax.coindocker.rpc;

import com.neemre.btcdcli4j.core.domain.Payment;
import com.neemre.btcdcli4j.core.domain.PaymentOverview;
import com.neemre.btcdcli4j.core.domain.Transaction;
import com.neemre.btcdcli4j.core.domain.enums.PaymentCategories;
import com.ourdax.coindocker.block.Block;
import com.ourdax.coindocker.block.BlockTrans;
import com.ourdax.coindocker.block.SimpleBlock;
import com.ourdax.coindocker.block.TransInfo;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author think on 14/1/2018
 */
public abstract class AbstractRpcProcessor implements RpcProcessor {

  @Override
  public BlockTrans queryTransSince(Block block) {
    BigInteger nextBlockNumber = new BigInteger(block.getBlockNumber()).add(BigInteger.ONE);
    return queryTransByBlockNumber(String.valueOf(nextBlockNumber.toString()));
  }

  @Override
  public RpcBatchTransferResponse batchTransfer(RpcBatchTransferRequest request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getConfirmationNum(Block block) {
    Block latestBlock = getLatestBlock();
    return new BigInteger(latestBlock.getBlockNumber()).subtract(
        new BigInteger(block.getBlockNumber())).intValue();
  }

  private BlockTrans queryTransByBlockNumber(String blockNumber) {
    Block block = new SimpleBlock(blockNumber, null);
    return queryTrans(block);
  }

  @Override
  public void preTransfer(RpcTransRequest rpcTransRequest) {

  }

  @Override
  public void preBatchTransfer(RpcBatchTransferRequest request) {

  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  protected TransInfo adaptTransInfo(PaymentOverview payment, Transaction transaction) {
    TransInfo transInfo = new TransInfo();
    transInfo.setAmount(payment.getAmount().abs());
    transInfo.setTo(payment.getAddress());
    transInfo.setBlockHash(transaction.getBlockHash());
    transInfo.setConfirmNum(transaction.getConfirmations());
    transInfo.setTxId(transaction.getTxId());
    transInfo.setVout(String.valueOf(payment.getVOut()));
    transInfo.setDirection(getDirection(payment));
    return transInfo;
  }

  protected TransInfo adaptTransInfo(Payment payment) {
    TransInfo transInfo = new TransInfo();
    transInfo.setAmount(payment.getAmount());
    transInfo.setTo(payment.getAddress());
    transInfo.setBlockHash(payment.getBlockHash());
    transInfo.setConfirmNum(payment.getConfirmations());
    transInfo.setTxId(payment.getTxId());
    transInfo.setVout(String.valueOf(payment.getVOut()));
    return transInfo;
  }
  private TransInfo.Direction getDirection(PaymentOverview paymentOverview) {
    PaymentCategories category = paymentOverview.getCategory();
    if (category == PaymentCategories.SEND) {
      return TransInfo.Direction.OUT;
    }
    if (category == PaymentCategories.RECEIVE) {
      return TransInfo.Direction.IN;
    }
    return null;
  }

}
