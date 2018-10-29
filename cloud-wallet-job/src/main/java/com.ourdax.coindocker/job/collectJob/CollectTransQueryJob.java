package com.ourdax.coindocker.job.collectJob;

import com.ourdax.coindocker.AssetComponentManager;
import com.ourdax.coindocker.block.TransInfo;
import com.ourdax.coindocker.block.TransStatus;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.enums.CollectOrderStatus;
import com.ourdax.coindocker.domain.CollectFeeChargeRecord;
import com.ourdax.coindocker.domain.CollectFeeChargeRecord.TransferStatus;
import com.ourdax.coindocker.domain.CollectionRecords;
import com.ourdax.coindocker.job.AbstractJob;
import com.ourdax.coindocker.job.JobConfig;
import com.ourdax.coindocker.rpc.RpcProcessor;
import com.ourdax.coindocker.service.CollectFeeChargeRecordService;
import com.ourdax.coindocker.service.CollectionRecordsService;
import java.math.BigInteger;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zhangjinyang on 2018/7/22.
 */
@Component
@Slf4j
public class CollectTransQueryJob extends AbstractJob {

  private static final Integer VALID_CONFIRMATION = 6;
  @Autowired
  private AssetComponentManager manager;
  @Autowired
  private CollectionRecordsService collectionRecordsService;

  @Autowired
  private CollectFeeChargeRecordService feeChargeRecordService;

  @Override
  public void run(JobConfig config) {

    AssetCode assetCode = config.getAssetCode();
    log.info("Query collection records confirmation for {} start…………", assetCode);
    RpcProcessor rpcProcessor = manager.getRpcProcessor(assetCode);
    try {
      handleCollectionRecord(rpcProcessor, assetCode);
      handleCollectFeeCharge(rpcProcessor, assetCode);
    } catch (Exception e) {
      e.printStackTrace();
    }

    log.info("Query collection records confirmation for {} end…………", assetCode);

  }

  private void handleCollectFeeCharge(RpcProcessor rpcProcessor, AssetCode assetCode) {
    List<CollectFeeChargeRecord> pendingRecords = feeChargeRecordService
        .queryPendingRecords(assetCode);
    if (pendingRecords.isEmpty()) {
      return;
    }
    pendingRecords.stream().filter(record -> StringUtils.isNotEmpty(record.getTxId()))
        .forEach(record -> handleCollectFeeChargeResult(rpcProcessor, record));
  }

  @Transactional
  private void handleCollectFeeChargeResult(RpcProcessor rpcProcessor,
      CollectFeeChargeRecord record) {

    List<TransInfo> transInfos = rpcProcessor.queryTransInfo(record.getTxId());
    TransInfo transInfo = transInfos.get(0);
    String blockNumber = rpcProcessor.getLatestBlock().getBlockNumber();
    transInfo.setConfirmNum(BigInteger.valueOf(new Long(blockNumber))
        .subtract(BigInteger.valueOf(new Long(transInfo.getBlockNumber()))).intValue());
    TransStatus transStatus = rpcProcessor.getTransStatus(transInfo);
    switch (transStatus) {
      case FAILED:
        record.setTransactionStatus(TransferStatus.FAIL);
        break;
      case COMPLETED:
        record.setTransactionStatus(TransferStatus.CONFIRMED);
        updateCollectionRecordStatus(record);
        break;
      case CONFIRMING:
        record.setTxFee(transInfo.getFee());
        break;
      default:
        throw new AssertionError("Impossible");
    }
    feeChargeRecordService.updateStatus(record);

  }

  private void updateCollectionRecordStatus(CollectFeeChargeRecord record) {

    collectionRecordsService
        .updateRecordsStatusById(record.getCollectionId(), CollectOrderStatus.NEW);

  }

  private void handleCollectionRecord(RpcProcessor rpcProcessor, AssetCode assetCode) {

    List<CollectionRecords> pendingList = collectionRecordsService.queryPendingRecords(assetCode);
    if (pendingList.isEmpty()) {
      return;
    }
    pendingList.stream().filter(record -> StringUtils.isNotEmpty(record.getTxId()))
        .forEach(record -> handleCollectionTransResult(rpcProcessor, record));

  }

  private void handleCollectionTransResult(RpcProcessor rpcProcessor, CollectionRecords record) {

    log.info("查询hash={}的交易结果", record.getTxId());
    List<TransInfo> transInfos = rpcProcessor.queryTransInfo(record.getTxId());
    if (transInfos.isEmpty()) {
      return;
    }
    TransInfo transInfo = transInfos.get(0);
    String blockNumber = rpcProcessor.getLatestBlock().getBlockNumber();
    transInfo.setConfirmNum(BigInteger.valueOf(new Long(blockNumber))
        .subtract(BigInteger.valueOf(new Long(transInfo.getBlockNumber()))).intValue());
    TransStatus transStatus = checkTransStatus(transInfo);
    switch (transStatus) {
      case FAILED:
        record.setStatus(CollectOrderStatus.FAILED);
        break;
      case COMPLETED:
        record.setStatus(CollectOrderStatus.CONFIRMED);
        break;
      case CONFIRMING:
        record.setFee(transInfo.getFee());
        break;
      default:
        throw new AssertionError("Impossible");
    }
    collectionRecordsService.updateStatus(record);
    log.info("查询hash={}的交易结果结束，最新状态为{}", record.getStatus());
  }

  private TransStatus checkTransStatus(TransInfo transInfo) {

    if (transInfo.getConfirmNum() > VALID_CONFIRMATION) {
      return TransStatus.COMPLETED;
    } else {
      return TransStatus.CONFIRMING;
    }
  }

  @Override
  public String getName() {
    return CollectTransQueryJob.class.getSimpleName();
  }

}
