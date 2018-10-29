package com.ourdax.coindocker.job.majorjob;

import com.ourdax.coindocker.AssetComponentManager;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.domain.TransferOut;
import com.ourdax.coindocker.job.AbstractJob;
import com.ourdax.coindocker.job.JobConfig;
import com.ourdax.coindocker.service.TransferOutService;
import com.ourdax.coindocker.trans.TransHandler;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author think on 23/1/2018
 */
@Component
@Slf4j
public class TransferOutSendingJob extends AbstractJob {

  @Autowired
  private TransferOutService transferOutService;

  @Autowired
  private AssetComponentManager manager;

  @Override
  public String getName() {
    return TransferOutSendingJob.class.getSimpleName();
  }

  public void run(JobConfig config) {
    try {
      AssetCode assetCode = config.getAssetCode();
      log.info("TransferOutSendingJob sending {} transfer out transactions...", assetCode);
      List<TransferOut> unsents = transferOutService.queryUnsents(assetCode);
      log.info("{} {} transfer out transactions need to send", unsents.size(), assetCode);
      if (CollectionUtils.isEmpty(unsents)) {
        return;
      }
      TransHandler transHandler = manager.getTransHandler(assetCode);
      transfer(transHandler, unsents);
    } catch (Exception e) {
      log.error("TransferOutSendingJob execute error", e);
    }
  }

  private void transfer(TransHandler transHandler, List<TransferOut> transferOuts) {
    try {
      transHandler.transfer(transferOuts);
    } catch (Exception e) {
      log.error("send transfer transaction error", e);
    }
  }
}
