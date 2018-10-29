package com.ourdax.coindocker.trans;

import static com.google.common.base.Strings.nullToEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import com.ourdax.coindocker.AssetComponentManager;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.block.TransInfo.Direction;
import com.ourdax.coindocker.block.TransStatus;
import com.ourdax.coindocker.common.base.AssetException;
import com.ourdax.coindocker.common.base.RpcException;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.enums.AssetStatus;
import com.ourdax.coindocker.common.utils.DateUtil;
import com.ourdax.coindocker.domain.TransferIn;
import com.ourdax.coindocker.domain.TransferIn.DepositStatus;
import com.ourdax.coindocker.domain.TransferOut;
import com.ourdax.coindocker.domain.TransferOut.WithdrawStatus;
import com.ourdax.coindocker.mq.messages.TransferResult;
import com.ourdax.coindocker.rpc.RpcBatchTransferRequest;
import com.ourdax.coindocker.rpc.RpcBatchTransferResponse;
import com.ourdax.coindocker.rpc.RpcProcessor;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import com.ourdax.coindocker.service.CoinAddressService;
import com.ourdax.coindocker.service.NotifyService;
import com.ourdax.coindocker.service.TransferInService;
import com.ourdax.coindocker.service.TransferOutService;
import com.ourdax.coindocker.utils.TransactionHelper;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * @author think on 14/1/2018
 */
@Slf4j
public abstract class DefaultTransHandler extends AbstractTransHandler {

  @Autowired
  protected AssetComponentManager manager;

  @Autowired
  protected TransferInService transferInService;

  @Autowired
  protected TransferOutService transferOutService;

  @Autowired
  protected CoinAddressService coinAddressService;

  @Autowired
  protected TransactionHelper transactionHelper;

  @Autowired
  protected NotifyService notifyService;


  private static final Predicate<TransInfo> POSITIVE_AMOUNT_FILTER =
      (transInfo -> transInfo.getAmount().compareTo(BigDecimal.ZERO) > 0);

  protected Predicate<TransInfo> transFilter() {
    return POSITIVE_AMOUNT_FILTER;
  }

  @Override
  public void receiveNewTrans(AssetCode assetCode, TransInfo transInfo) {

    log.debug("Receiving new transaction of {}, transaction: {}", assetCode, transInfo);
    if (!isToAddressInPool(assetCode, transInfo.getTo())) {
      return;
    }

    if (!transFilter().test(transInfo)) {
      return;
    }

    /**到这里开始将合约币的币种名称改为其真正的币种名。这里可以再优化。不能一种合约就添加一个判断*/
    if (transInfo.getAssetCode() != null && (transInfo.getAssetCode().isQRC20() || transInfo
        .getAssetCode().isActContract())) {
      assetCode = transInfo.getAssetCode();
    }

    if (!transferInService.isTransExist(
        assetCode, transInfo.getTxId(), transInfo.getTo(), nullToEmpty(transInfo.getVout()))) {
      log.info("New transaction of {} found, transInfo: {}", assetCode, transInfo);
      onOurTransReceived(assetCode, transInfo);
    } else {
      log.info("Transfer in already handled, trans: {}", transInfo);
    }
  }

  protected boolean isToAddressInPool(AssetCode assetCode, String toAddress) {
    AssetCode dbAssetCode = assetCode;
    if (assetCode.isERC20()) {
      dbAssetCode = AssetCode.ERC20;
    }
    if (assetCode.isQRC20()) {
      dbAssetCode = assetCode.QRC20;
    }
    if (assetCode.isActContract()) {
      dbAssetCode = assetCode.ACTCONTRACT;
    }

    return coinAddressService.getCoinAddress(dbAssetCode, toAddress).isPresent();
  }

  protected void onOurTransReceived(AssetCode assetCode, TransInfo transInfo) {
    transactionHelper.doInCurrentTransaction(() -> {
      TransferIn transferIn = new TransferIn();
      transferIn.setAssetCode(assetCode.name());
      transferIn.setDepositStatus(DepositStatus.NEW);
      transferIn.setTxId(transInfo.getTxId());
      transferIn.setAmount(transInfo.getAmount());
      transferIn.setConfirmNum(0);
      transferIn.setBlockNum(transInfo.getBlockNumber());
      transferIn.setBlockhash(transInfo.getBlockHash());
      transferIn.setFromCoinAddress(transInfo.getFrom());
      transferIn.setToCoinAddress(transInfo.getTo());
      transferIn.setVout(nullToEmpty(transInfo.getVout()));
      transferIn.setCategory("receive");
      transferIn.setFailMessage(StringUtils.EMPTY);
      transferInService.save(assetCode, transferIn);
      log.info("Save new transaction: {}", transferIn);

      // send notification
      TransferResult notification = new TransferResult();
      notification.setAssetCode(assetCode.name());
      notification.setAssetStatus(AssetStatus.CONFIRM.name());
      notification.setToWallet(transInfo.getTo());
      notification.setAmount(transferIn.getAmount());
      notification.setSendMessage(StringUtils.EMPTY);
      notification.setOrderId(genOrderId(assetCode, transferIn.getId()));
      notification.setTxId(transferIn.getTxId());
      notifyService.sendTransferInNotification(assetCode, notification);
    });
  }

  @Override
  public void transfer(TransferOut transferOut) {
    log.info("Do real transfer out, {}", transferOut);
    AssetCode assetCode = getAssetCode();
    RpcProcessor rpcProcessor = manager.getRpcProcessor(assetCode);
    try {
      RpcTransRequest rpcTransRequest = buildRpcTransRequest(transferOut);
      rpcProcessor.preTransfer(rpcTransRequest);

      transferOut.setWithdrawStatus(WithdrawStatus.CONFIRMING);
      transferOut.setUpdateDate(DateUtil.now());
      transactionHelper.doInNewTransaction(() -> {
        transferOutService.updateWithdrawStatusById(assetCode, transferOut);
      });

      RpcTransResponse rpcTransResponse = rpcProcessor.defaultTransfer(rpcTransRequest);
      transferOut.setTxId(rpcTransResponse.getTxId());
      transferOutService.updateTxId(assetCode, transferOut);
    } catch (AssetException e) {
      log.error("Transfer out error, request: {}", transferOut, e);
      String errorMsg = extractErrorMsg(e);
      if (errorMsg != null) {
        saveErrorMessage(assetCode, transferOut, errorMsg);
      }
    } catch (Exception e) {
      log.error("Transfer out unexpected error", e);
    }
  }

  private String extractErrorMsg(AssetException e) {
    if (e instanceof RpcException) {
      return e.getCause() == null ? null : e.getCause().getMessage();
    } else {
      return e.getMessage();
    }
  }

  protected RpcTransRequest buildRpcTransRequest(TransferOut transferOut) {
    RpcTransRequest rpcTransRequest = new RpcTransRequest();
    rpcTransRequest.setAmount(transferOut.getAmount());
    rpcTransRequest.setTo(transferOut.getToCoinAddress());
    if (getAssetCode().isContract()) {
      rpcTransRequest.setAssetCode(AssetCode.valueOf(transferOut.getAssetCode()));
    }
    return rpcTransRequest;
  }


  @Override
  public void transfer(List<TransferOut> batch) {
    if (CollectionUtils.isEmpty(batch)) {
      return;
    }
    if (getAssetCode().supportBatchTransfer()) {
      batchTransfer(batch);
    } else {
      super.transfer(batch);
    }
  }

  protected void batchTransfer(List<TransferOut> batch) {
    log.info("Sending batch transfer transaction: {}", batch);
    AssetCode assetCode = getAssetCode();
    RpcProcessor rpcProcessor = manager.getRpcProcessor(assetCode);
    RpcBatchTransferRequest rpcRequest = buildBatchRequest(batch);
    try {
      rpcProcessor.preBatchTransfer(rpcRequest);

      transactionHelper.doInNewTransaction(() ->
          batch.forEach(transferOut -> {
            transferOut.setWithdrawStatus(WithdrawStatus.CONFIRMING);
            transferOut.setUpdateDate(DateUtil.now());
            transferOutService.updateWithdrawStatusById(assetCode, transferOut);
          }));

      RpcBatchTransferResponse response = rpcProcessor.batchTransfer(rpcRequest);
      log.info("Send batch batch transfer return result: {}", response);
      batch.forEach(transferOut -> {
        transferOut.setTxId(response.getTxId());
        transferOutService.updateTxId(assetCode, transferOut);
      });
    } catch (AssetException e) {
      log.error("Batch Transfer out error, request: {}", rpcRequest, e);
      String errorMsg = extractErrorMsg(e);
      if (errorMsg != null) {
        batch.forEach(transferOut -> saveErrorMessage(assetCode, transferOut, errorMsg));
      }
    } catch (Exception e) {
      log.error("Batch Transfer out unexpected error, request: {}", rpcRequest, e);
    }
  }

  private RpcBatchTransferRequest buildBatchRequest(List<TransferOut> batch) {

    RpcBatchTransferRequest request = new RpcBatchTransferRequest();
    request.setBatchRequests(
        batch.stream().map(this::buildRpcTransRequest).collect(Collectors.toList()));
    return request;
  }


  @Override
  public final void queryTransferOut(TransferOut transferOut) {

    AssetCode assetCode = getAssetCode();
    log.info("Query transfer out transaction status, transfer out id: {}", transferOut.getId());
    RpcProcessor rpcProcessor = manager.getRpcProcessor(assetCode);

    List<TransInfo> transInfos1 = rpcProcessor.queryTransInfo(transferOut.getTxId());
    List<TransInfo> transInfos = transInfos1.stream()
        .filter(transInfo -> {
          if (!equalsIgnoreCase(transInfo.getTo(), transferOut.getToCoinAddress())) {
            return false;
          }
          return transInfo.getDirection() == null || transInfo.getDirection() == Direction.OUT;
        })
        .collect(Collectors.toList());
    log.info("Query transfer out transaction return: {}", transInfos);
    if (transInfos.isEmpty()) {
      log.warn("No transInfo found for transfer out, id: {}", transferOut.getId());
      return;
    }

    TransInfo transInfo = transInfos.get(0);
    TransStatus transStatus = rpcProcessor.getTransStatus(transInfo);

    /**适配合约币种*/
    if (assetCode.isContract()) {
      assetCode = AssetCode.valueOf(transferOut.getAssetCode());
    }

    switch (transStatus) {
      case FAILED:
        onTransferOutFailure(assetCode, transferOut, null);
        break;
      case COMPLETED:
        onTransferOutSuccess(assetCode, transInfo, transferOut);
        break;
      case CONFIRMING:
        onTransferOutConfirming(assetCode, transInfo, transferOut);
        break;
      default:
        throw new AssertionError("Impossible");
    }
  }


  @Override
  public final void queryTransferIn(TransferIn transferIn) {

    AssetCode assetCode = getAssetCode();
    log.info("Query transfer in transaction status, transfer in id: {}", transferIn.getId());
    RpcProcessor rpcProcessor = manager.getRpcProcessor(assetCode);
    List<TransInfo> transInfos = rpcProcessor.queryTransInfo(transferIn.getTxId()).stream()
        .filter((transInfo -> {
          if (!equalsIgnoreCase(transInfo.getTo(), transferIn.getToCoinAddress())) {
            return false;
          }
          if (!StringUtils
              .equals(nullToEmpty(transferIn.getVout()), nullToEmpty(transInfo.getVout()))) {
            return false;
          }
          return transInfo.getDirection() == null || transInfo.getDirection() == Direction.IN;
        }))
        .collect(Collectors.toList());

    log.info("Query transfer in return transferInfos: {}", transInfos);
    if (transInfos.isEmpty()) {
      log.warn("No transInfo found for transfer in query, id: {}", transferIn.getId());
      return;
    }

    if (transInfos.size() > 1) {
      log.error("Unexpected multi transferIn in the same transaction, transInfos: {}", transInfos);
    }

    TransInfo transInfo = transInfos.get(0);
    TransStatus transStatus = rpcProcessor.getTransStatus(transInfo);

    /**适配合约代币*/
    if (assetCode.isContract()) {
      assetCode = AssetCode.valueOf(transferIn.getAssetCode());
    }

    switch (transStatus) {
      case FAILED:
        onTransferInFailure(assetCode, transferIn);
        break;
      case COMPLETED:
        onTransferInSuccess(assetCode, transInfo, transferIn);
        break;
      case CONFIRMING:
        onTransferInConfirming(assetCode, transInfo, transferIn);
        break;
      default:
        throw new AssertionError("Impossible");
    }
  }

  private String genOrderId(AssetCode assetCode, int id) {
    return assetCode + "_" + id;
  }

  protected void onTransferInSuccess(AssetCode assetCode, TransInfo transInfo,
      TransferIn transferIn) {
    log.info("{} transfer in transaction confirm success, id: {}", transferIn.getId());
    transactionHelper.doInCurrentTransaction(() -> {
      RpcProcessor rpcProcessor = manager.getRpcProcessor(assetCode);
      transferIn.setDepositStatus(DepositStatus.CONFIRMED);
      transferIn.setConfirmNum(getConfirmNum(transInfo, rpcProcessor));
      transferIn.setBlockNum(transInfo.getBlockNumber());
      transferIn.setBlockhash(transInfo.getBlockHash());
      transferIn.setUpdateDate(DateUtil.now());
      transferInService.updateTransferStatus(assetCode, transferIn);

      TransferResult notification = new TransferResult();
      notification.setAssetCode(assetCode.name());
      notification.setAssetStatus(AssetStatus.SUCCESS.name());
      notification.setAmount(transInfo.getAmount());
      notification.setOrderId(genOrderId(assetCode, transferIn.getId()));
      notification.setToWallet(transferIn.getToCoinAddress());
      notification.setTxId(transferIn.getTxId());
      notifyService.sendTransferInNotification(assetCode, notification);
    });

  }

  private int getConfirmNum(TransInfo transInfo, RpcProcessor rpcProcessor) {
    if (transInfo.getConfirmNum() != null) {
      return transInfo.getConfirmNum();
    } else {
      return rpcProcessor.getConfirmationNum(transInfo);
    }
  }

  protected void onTransferInFailure(AssetCode assetCode, TransferIn transferIn) {
    log.error("{} transfer in transaction failed, id: {}", assetCode, transferIn.getId());
    transactionHelper.doInCurrentTransaction(() -> {
      transferIn.setDepositStatus(DepositStatus.FAIL);
      transferIn.setUpdateDate(DateUtil.now());
      transferIn.setConfirmNum(0);
      transferInService.updateTransferStatus(assetCode, transferIn);

      TransferResult notification = new TransferResult();
      notification.setAssetCode(assetCode.name());
      notification.setAssetStatus(AssetStatus.FAILURE.name());
      notification.setAmount(transferIn.getAmount());
      notification.setOrderId(genOrderId(assetCode, transferIn.getId()));
      notification.setToWallet(transferIn.getToCoinAddress());
      notification.setTxId(transferIn.getTxId());
      notifyService.sendTransferInNotification(assetCode, notification);
    });
  }

  protected void onTransferInConfirming(AssetCode assetCode, TransInfo transInfo,
      TransferIn transferIn) {
    RpcProcessor rpcProcessor = manager.getRpcProcessor(assetCode);
    transferIn.setDepositStatus(DepositStatus.CONFIRMING);
    transferIn.setUpdateDate(DateUtil.now());
    transferIn.setBlockhash(transferIn.getBlockhash());
    transferIn.setConfirmNum(getConfirmNum(transInfo, rpcProcessor));
    transferInService.updateTransferStatus(assetCode, transferIn);
    log.info("{} transfer in transaction still confirming, id: {}, current confirm num: {}",
        assetCode, transferIn.getId(), transferIn.getConfirmNum());
  }

  protected void onTransferOutSuccess(AssetCode assetCode, TransInfo transInfo,
      TransferOut transferOut) {
    log.info("{} transfer out transaction confirm success, id: {}", transferOut.getId());

    transactionHelper.doInCurrentTransaction(() -> {
      RpcProcessor rpcProcessor = manager.getRpcProcessor(assetCode);
      transferOut.setWithdrawStatus(WithdrawStatus.CONFIRMED);
      transferOut.setConfirmNum(getConfirmNum(transInfo, rpcProcessor));
      transferOut.setTxNum(String.valueOf(transInfo.getBlockNumber()));
      transferOut.setFailMessage(StringUtils.EMPTY);
      transferOut.setErrorMessage(StringUtils.EMPTY);
      transferOut.setUpdateDate(DateUtil.now());
      transferOutService.updateWithdrawStatusById(assetCode, transferOut);

      TransferResult notification = new TransferResult();
      notification.setAssetCode(assetCode.name());
      notification.setAssetStatus(AssetStatus.SUCCESS.name());
      notification.setAmount(transferOut.getAmount());
      notification.setOrderId(genOrderId(assetCode, transferOut.getId()));
      notification.setInnerOrderId(transferOut.getInnerOrderNo());
      notification.setToWallet(transferOut.getToCoinAddress());
      notification.setTxId(transferOut.getTxId());
      notifyService.sendTransferOutNotification(assetCode, notification);
    });

  }

  protected void onTransferOutFailure(AssetCode assetCode, TransferOut transferOut, Throwable ex) {
    log.error("{} transfer out transaction failed, id: {}", assetCode, transferOut.getId(), ex);
    transactionHelper.doInCurrentTransaction(() -> {
      transferOut.setWithdrawStatus(WithdrawStatus.FAIL);
      transferOut.setUpdateDate(DateUtil.now());
      transferOut.setFailMessage(ex == null ? StringUtils.EMPTY : nullToEmpty(ex.getMessage()));
      transferOutService.updateWithdrawStatusById(assetCode, transferOut);

      TransferResult notification = new TransferResult();
      notification.setAssetCode(assetCode.name());
      notification.setAssetStatus(AssetStatus.FAILURE.name());
      notification.setAmount(transferOut.getAmount());
      notification.setOrderId(genOrderId(assetCode, transferOut.getId()));
      notification.setInnerOrderId(transferOut.getInnerOrderNo());
      notification.setToWallet(transferOut.getToCoinAddress());
      notification.setSendMessage(transferOut.getFailMessage());
      notification.setTxId(transferOut.getTxId());
      notifyService.sendTransferOutNotification(assetCode, notification);
    });
  }


  protected void onTransferOutConfirming(AssetCode assetCode, TransInfo transInfo,
      TransferOut transferOut) {
    RpcProcessor rpcProcessor = manager.getRpcProcessor(assetCode);
    transferOut.setConfirmNum(getConfirmNum(transInfo, rpcProcessor));
    transferOut.setUpdateDate(DateUtil.now());
    transferOut.setWithdrawStatus(WithdrawStatus.CONFIRMING);
    transferOutService.updateWithdrawStatusById(assetCode, transferOut);
    log.info("{} transfer out transaction still confirming, id: {}, current confirm num: {}",
        assetCode, transferOut.getId(), transferOut.getConfirmNum());
  }


  private void saveErrorMessage(AssetCode assetCode, TransferOut transferOut, String message) {
    transferOut.setErrorMessage(message);
    transferOut.setUpdateDate(DateUtil.now());
    transferOutService.updateWithdrawStatusById(assetCode, transferOut);
  }
}
