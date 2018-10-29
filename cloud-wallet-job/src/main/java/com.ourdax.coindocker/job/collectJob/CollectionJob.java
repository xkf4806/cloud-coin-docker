package com.ourdax.coindocker.job.collectJob;

import com.alibaba.fastjson.JSON;
import com.ourdax.coindocker.AssetComponentManager;
import com.ourdax.coindocker.common.base.AssetException;
import com.ourdax.coindocker.common.base.RpcException;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.enums.CollectOrderStatus;
import com.ourdax.coindocker.config.CollectionConfig;
import com.ourdax.coindocker.domain.CollectionRecords;
import com.ourdax.coindocker.job.AbstractJob;
import com.ourdax.coindocker.job.JobConfig;
import com.ourdax.coindocker.rpc.RpcProcessor;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import com.ourdax.coindocker.service.CollectionRecordsService;
import com.ourdax.coindocker.service.OfflineAddressService;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zhangjinyang on 2018/7/17.
 */
@Component
@Slf4j
public class CollectionJob extends AbstractJob {

  @Autowired
  private CollectionRecordsService collectionRecordsService;

  @Autowired
  private OfflineAddressService offlineAddressService;

  @Autowired
  private AssetComponentManager manager;

  @Override
  public void run(JobConfig config) {

    AssetCode assetCode = config.getAssetCode();
    List<CollectionRecords> unHandledRecords = collectionRecordsService
        .queryUnHandledRecords(assetCode);
    if (unHandledRecords.isEmpty()) {
      log.info("There is no records need to be handled, assetCode={}", assetCode);
      return;
    }

    RpcProcessor rpcProcessor = manager.getRpcProcessor(assetCode);
    unHandledRecords.stream().forEach(record -> {
      log.info("处理记录" + JSON.toJSONString(record));

      String targetAddress = offlineAddressService
          .fetchUsefulOfflineAddress(assetCode, record.getAmount());
      record.setTargetAddr(targetAddress);
      record.setStatus(CollectOrderStatus.CONFIRMING);

      try {
        log.info("提交交易{}", JSON.toJSONString(record));
        RpcTransResponse transResponse = rpcProcessor
            .transferFrom(record.getSourceAddr(), record.getTargetAddr(), record.getAmount());
        updateRecordsByTransResponse(transResponse.getTxId(), record);
        log.info("交易处理完成，交易hash={}", transResponse.getTxId());
      } catch (AssetException e) {
        log.error("Transfer out error, request: from={}, to={}, amount={}", record.getSourceAddr(),
            record.getTargetAddr(), record.getAmount(), e);
        String errorMsg = extractErrorMsg(e);
        if (errorMsg != null) {
          updateRecordsByErrorMessage(record, errorMsg);
        }
      } catch (Exception e) {
        log.error("Transfer out unexpected error", e);
      }
      log.info("处理记录" + JSON.toJSONString(record) + "完成" );
    });

  }

  private void updateRecordsByTransResponse(String txId, CollectionRecords records) {
    records.setTxId(txId);
    records.setStatus(CollectOrderStatus.CONFIRMING);
    collectionRecordsService.updateStatus(records);
  }

  @Override
  public String getName() {
    return CollectionJob.class.getSimpleName();
  }

  private String extractErrorMsg(AssetException e) {
    if (e instanceof RpcException) {
      return e.getCause() == null ? null : e.getCause().getMessage();
    } else {
      return e.getMessage();
    }
  }

  private void updateRecordsByErrorMessage(CollectionRecords records, String errorMsg) {
    records.setErrorMsg(errorMsg);
    collectionRecordsService.updateStatus(records);
  }


}
