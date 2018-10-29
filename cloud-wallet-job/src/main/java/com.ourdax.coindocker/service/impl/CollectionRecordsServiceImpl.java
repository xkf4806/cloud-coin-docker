package com.ourdax.coindocker.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ourdax.coindocker.AssetComponentManager;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.enums.CollectOrderStatus;
import com.ourdax.coindocker.config.CollectionConfig;
import com.ourdax.coindocker.dao.CollectConfigDao;
import com.ourdax.coindocker.dao.CollectionRecordsDao;
import com.ourdax.coindocker.domain.CollectConfig;
import com.ourdax.coindocker.domain.CollectionRecords;
import com.ourdax.coindocker.domain.CollectionRecords.CollectType;
import com.ourdax.coindocker.domain.CollectionRecords.TargetAddressType;
import com.ourdax.coindocker.domain.TransferIn;
import com.ourdax.coindocker.rpc.RpcProcessor;
import com.ourdax.coindocker.service.CollectionRecordsService;
import com.ourdax.coindocker.service.OfflineAddressService;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zhangjinyang on 2018/7/18.
 */
@Slf4j
@Service
public class CollectionRecordsServiceImpl implements CollectionRecordsService {

  @Autowired
  private CollectConfigDao collectConfigDao;

  @Autowired
  private CollectionRecordsDao collectionRecordsDao;

  @Autowired
  private OfflineAddressService offlineAddressService;

  @Autowired
  private AssetComponentManager processorManager;

  @Autowired
  private CollectionConfig collectionConfig;

  @Transactional
  @Override
  public void checkIfNeedToCollectAndSave(AssetCode assetCode, TransferIn transferIn) {

    log.info("校验 {} 账户（地址）{} 是否需要归集", assetCode, transferIn.getInAddress());
    RpcProcessor rpcProcessor = processorManager.getRpcProcessor(assetCode);
    BigDecimal balance = rpcProcessor.queryBalance(transferIn.getInAddress());
    String uniformAccount = rpcProcessor.getUniformAccount();
    CollectConfig collectConfig = collectConfigDao.queryConfigsByAsset(assetCode.name());
    CollectionRecords record = buildCollectionRecord(collectConfig, uniformAccount, transferIn,
        balance, assetCode, rpcProcessor);
    if (record == null) {
      return;
    }

    collectionRecordsDao.insert(record);

  }

  private CollectionRecords buildCollectionRecord(CollectConfig collectConfig,
      String uniformAccount,
      TransferIn transferIn, BigDecimal balance, AssetCode assetCode, RpcProcessor rpcProcessor) {

    CollectionRecords record = whereTobeCollected(collectConfig, uniformAccount, transferIn,
        balance, assetCode);
    record = checkGas(record, assetCode, rpcProcessor);
    return record;

  }

  /**Only erc20 now */
  private CollectionRecords checkGas(CollectionRecords record, AssetCode assetCode,
      RpcProcessor rpcProcessor) {

    BigDecimal inAddressGasBalance = rpcProcessor
        .queryChainMajorTokenBalance(record.getSourceAddr());
    if (assetCode.isERC20()
        && new BigDecimal(collectionConfig.getEthFeeBasic()).compareTo(inAddressGasBalance) > 0) {
      log.info("归集地址 " + record.getSourceAddr() + "为ERC20, 转账手续费不足, 记录为待充值gas状态");
      record.setStatus(CollectOrderStatus.UNCHARGE);
    }

    return record;

  }

  private CollectionRecords whereTobeCollected(CollectConfig collectConfig, String uniformAccount,
      TransferIn transferIn, BigDecimal balance, AssetCode assetCode) {

    if (balance.compareTo(collectConfig.getLowerLimit()) <= 0) {
      log.info("余额{}小于{}, 所以 {} 账户（地址）{} 不需要归集", balance, collectConfig.getLowerLimit(),
          assetCode, transferIn.getInAddress());
      return null;
    }

    CollectionRecords record = new CollectionRecords();
    if (balance.compareTo(collectConfig.getUpperLimit()) >= 0) {
      log.info("余额{}大于upperLimit {}, 所以 {} 账户（地址）{} 归集到 冷钱包", balance,
          collectConfig.getUpperLimit(), assetCode,
          transferIn.getInAddress());
      record.setTargetType(TargetAddressType.TO_COLD_WALLET);
    } else if (balance.compareTo(collectConfig.getUpperLimit()) < 0
        && balance.compareTo(collectConfig.getLowerLimit()) > 0) {
      log.info("余额{}小于于upperLimit {}， 大于lowerLimit {} , 所以 {} 账户（地址）{} 归集到 热钱包",
          balance, collectConfig.getUpperLimit(), collectConfig.getLowerLimit(), assetCode,
          transferIn.getInAddress());
      record.setTargetAddr(uniformAccount);
      record.setTargetType(TargetAddressType.TO_HOT_WALLET);
    }

    return fulfillingRecord(record, assetCode, balance, transferIn);

  }


  private CollectionRecords fulfillingRecord(CollectionRecords record, AssetCode assetCode,
      BigDecimal balance, TransferIn transferIn) {

    record.setAssetCode(assetCode.name());
    record.setAmount(balance);
    record.setFee(transferIn.getFee());
    record.setSourceAddr(transferIn.getToCoinAddress());
    record.setStatus(CollectOrderStatus.NEW);
    record.setCollectType(CollectType.FROM_USER);
    return record;

  }

  @Override
  public List<CollectionRecords> queryUnHandledRecords(AssetCode assetCode) {
    return collectionRecordsDao
        .queryRecordsByAssetAndStatus(assetCode, Lists.newArrayList(CollectOrderStatus.NEW));
  }

  @Transactional
  @Override
  public int updateStatus(CollectionRecords record) {

    if (TargetAddressType.TO_COLD_WALLET.equals(record.getTargetType())) {
      offlineAddressService.updateAmountAndStatus(record);
    }

    return collectionRecordsDao.update(record);
  }

  @Override
  public List<CollectionRecords> queryPendingRecords(AssetCode assetCode) {
    return collectionRecordsDao
        .queryRecordsByAssetAndStatus(assetCode, Lists.newArrayList(CollectOrderStatus.CONFIRMING));
  }

  @Override
  public List<CollectionRecords> queryUnChargedRecords(AssetCode assetCode) {
    return collectionRecordsDao
        .queryRecordsByAssetAndStatus(assetCode, Lists.newArrayList(CollectOrderStatus.UNCHARGE));
  }

  @Override
  public int updateRecordsStatusById(Integer collectionId, CollectOrderStatus status) {
    return collectionRecordsDao.updateStatusById(collectionId, status);
  }
}
