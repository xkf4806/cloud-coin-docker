package com.ourdax.coindocker.job.collectJob;

import com.google.common.collect.Lists;
import com.ourdax.coindocker.AssetComponentManager;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.common.enums.CollectOrderStatus;
import com.ourdax.coindocker.dao.CollectConfigDao;
import com.ourdax.coindocker.dao.CollectionRecordsDao;
import com.ourdax.coindocker.domain.CollectConfig;
import com.ourdax.coindocker.domain.CollectionRecords;
import com.ourdax.coindocker.domain.CollectionRecords.CollectType;
import com.ourdax.coindocker.domain.EmailObj;
import com.ourdax.coindocker.job.AbstractJob;
import com.ourdax.coindocker.job.JobConfig;
import com.ourdax.coindocker.rpc.RpcProcessor;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import com.ourdax.coindocker.service.EmailService;
import com.ourdax.coindocker.service.OfflineAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by zhangjinyang on 2018/7/17.
 */
@Component
@Slf4j
public class UniformAccountMonitorJob extends AbstractJob {

  @Autowired
  private AssetComponentManager manager;

  @Autowired
  private CollectConfigDao collectConfigDao;

  @Autowired
  private CollectionRecordsDao collectionRecordsDao;

  @Autowired
  private OfflineAddressService offlineAddressService;

  @Autowired
  private EmailService emailService;

  @Value("${cold.wallet.manager.email}")
  private String walletManagerEmailAddress;


  @Override
  public void run(JobConfig config) {

    AssetCode assetCode = config.getAssetCode();
    log.info("uniform account monitor job for {} start……", assetCode);
    RpcProcessor rpcProcessor = manager.getRpcProcessor(assetCode);
    String uniformAccount = rpcProcessor.getUniformAccount();
    BigDecimal uniformAccountBalance = rpcProcessor.queryBalance();
    CollectConfig collectConfig = collectConfigDao.queryConfigsByAsset(assetCode.name());
    BigDecimal max = collectConfig.getUniformAccountMax();
    BigDecimal min = collectConfig.getUniformAccountMin();

    handleUniformAccount(rpcProcessor, uniformAccountBalance, min, max, assetCode, uniformAccount);
    log.info("uniform account monitor job for {} end……", assetCode);

  }

  private void handleUniformAccount(RpcProcessor rpcProcessor, BigDecimal uniformAccountBalance, BigDecimal min,
      BigDecimal max, AssetCode assetCode, String uniformAccount) {

    if (uniformAccountBalance.compareTo(max) > 0) {
      BigDecimal dValue = uniformAccountBalance.subtract(min);
      String address = offlineAddressService.fetchUsefulOfflineAddress(assetCode, dValue);
      /**若统一转出地址余额大于最大值，则将（余额-最小值）的资产转移到冷钱包*/
      RpcTransRequest request = new RpcTransRequest();
      request.setTo(address);
      request.setAmount(dValue);
      request.setAssetCode(assetCode);
      request.setFrom(uniformAccount);
      RpcTransResponse response = rpcProcessor.defaultTransfer(request);
      CollectionRecords records = buildCollectionRecords(response, request);
      collectionRecordsDao.insert(records);
    }

    if (uniformAccountBalance.compareTo(min) < 0){
      /*发送邮件给管理者**/
      EmailObj emailObj = new EmailObj();
      emailObj.setSubject(assetCode + "统一转出地址余额不足通知");
      emailObj.setToUser(Lists.newArrayList(walletManagerEmailAddress));
      emailObj.setText("MasterDax钱包服务上" + assetCode + "的统一转出地址余额低于" + min + "，请及时充值！");
      emailService.sendEmail(emailObj);
    }

  }

  private CollectionRecords buildCollectionRecords(RpcTransResponse response, RpcTransRequest request) {
    CollectionRecords record = new CollectionRecords();
    record.setStatus(CollectOrderStatus.NEW);
    record.setAmount(request.getAmount());
    record.setAssetCode(request.getAssetCode().name());
    record.setTargetAddr(request.getTo());
    record.setSourceAddr(request.getFrom());
    record.setCollectType(CollectType.UNIFORM_ACCOUNT);
    record.setTxId(response.getTxId());
    record.setStatus(CollectOrderStatus.CONFIRMING);
    return record;
  }

  @Override
  public String getName() {
    return UniformAccountMonitorJob.class.getSimpleName();
  }
}
