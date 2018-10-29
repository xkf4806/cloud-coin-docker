package com.ourdax.coindocker.job.collectJob;

import static com.google.common.base.MoreObjects.firstNonNull;

import com.alibaba.fastjson.JSON;
import com.ourdax.coindocker.AssetComponentManager;
import com.ourdax.coindocker.common.base.AssetException;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.config.CollectionConfig;
import com.ourdax.coindocker.domain.CollectFeeChargeRecord;
import com.ourdax.coindocker.domain.CollectFeeChargeRecord.TransferStatus;
import com.ourdax.coindocker.domain.CollectionRecords;
import com.ourdax.coindocker.job.AbstractJob;
import com.ourdax.coindocker.job.JobConfig;
import com.ourdax.coindocker.rpc.RpcProcessor;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import com.ourdax.coindocker.service.CollectFeeChargeRecordService;
import com.ourdax.coindocker.service.CollectionRecordsService;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zhangjinyang on 2018/7/17.
 */
@Component
@Slf4j
public class CollectionFeeRechargeJob extends AbstractJob {

  @Autowired
  private AssetComponentManager manager;

  @Autowired
  private CollectionRecordsService collectionRecordsService;

  @Autowired
  private CollectFeeChargeRecordService collectFeeChargeRecordService;

  @Autowired
  private CollectionConfig collectionConfig;

  @Override
  public void run(JobConfig config) {

    AssetCode assetCode = config.getAssetCode();
    log.info("handle fee recharge records for {} start……", assetCode);
    List<CollectionRecords> unchargedRecords = collectionRecordsService
        .queryUnChargedRecords(assetCode);

    RpcProcessor rpcProcessor;
    AssetCode chargeAsset;
    if (assetCode.isERC20()) {
      chargeAsset = AssetCode.ETH;
      rpcProcessor = manager.getRpcProcessor(AssetCode.ETH);
    } else if (AssetCode.USDT.equals(assetCode)) {
      chargeAsset = AssetCode.BTC;
      rpcProcessor = manager.getRpcProcessor(AssetCode.BTC);
    } else {
      log.info("非此程序中可归集的币种，asset={}", assetCode);
      return;
    }

    for (CollectionRecords record : unchargedRecords){
      CollectFeeChargeRecord feeChargeRecord = new CollectFeeChargeRecord();
      feeChargeRecord.setAssetCode(assetCode.name());
      feeChargeRecord.setAmount(record.getAmount());
      feeChargeRecord.setTxFee(firstNonNull(record.getFee(), BigDecimal.ZERO));
      feeChargeRecord.setFromAccount(StringUtils.EMPTY);
      feeChargeRecord.setFromCoinAddress(StringUtils.EMPTY);
      feeChargeRecord.setToCoinAddress(record.getTargetAddr());
      feeChargeRecord.setTransactionStatus(TransferStatus.CONFIRMING);
      feeChargeRecord.setTxId(StringUtils.EMPTY);
      feeChargeRecord.setErrorMessage(StringUtils.EMPTY);
      feeChargeRecord.setCollectionId(record.getId());

      RpcTransResponse transferResult;
      try {
        RpcTransRequest request = new RpcTransRequest();
        request.setAssetCode(chargeAsset);
        request.setTo(record.getSourceAddr());

        if (assetCode.isERC20()){
          request.setAmount(new BigDecimal(collectionConfig.getEthFeeBasic()).multiply(BigDecimal.valueOf(3)));
        } else if (AssetCode.USDT.equals(assetCode)){
          request.setAmount(new BigDecimal(collectionConfig.getBtcFeeBasic()).multiply(BigDecimal.valueOf(3)));
        } else {
          //todo There must be better way to optimize the part of code below
          String errorMessage = "币归集手续费充值，不支持的币种" + assetCode.name();
          log.error(errorMessage);
          feeChargeRecord.setErrorMessage(errorMessage);
          collectFeeChargeRecordService.saveFeeChargeRecord(feeChargeRecord);
          continue;
        }

        transferResult = rpcProcessor.defaultTransfer(request);
        feeChargeRecord.setTxId(transferResult.getTxId());

      } catch (AssetException e) {
        feeChargeRecord.setErrorMessage(e.getMessage());
        log.error("charge fee for {} collection failed. params: {}",
            assetCode, JSON.toJSONString(feeChargeRecord));
      } catch (Exception e) {
        feeChargeRecord.setErrorMessage(e.getMessage());
        log.error("charge fee for {} collection failed. params: {}",
            assetCode, JSON.toJSONString(feeChargeRecord));
      }

      collectFeeChargeRecordService.saveFeeChargeRecord(feeChargeRecord);
    }

    log.info("handle fee recharge records for {} end……", assetCode);
  }

  @Override
  public String getName() {
    return CollectionFeeRechargeJob.class.getSimpleName();
  }
}
