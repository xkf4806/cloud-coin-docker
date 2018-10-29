package com.ourdax.coindocker.job.majorjob;

import com.ourdax.coindocker.AssetComponentManager;
import com.ourdax.coindocker.domain.TransferIn;
import com.ourdax.coindocker.job.AbstractJob;
import com.ourdax.coindocker.job.JobConfig;
import com.ourdax.coindocker.service.TransferInService;
import com.ourdax.coindocker.trans.TransHandler;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author think on 11/1/2018
 */
@Component
@Slf4j
public class TransferInQueryJob extends AbstractJob {

  @Autowired
  private TransferInService transferInService;

  @Autowired
  private AssetComponentManager manager;

  @Override
  public String getName() {
    return TransferInQueryJob.class.getSimpleName();
  }

  @Override
  public void run(JobConfig config) {
    try {
      log.info("TransferInQueryJob querying {} transfer in transaction status...", config.getAssetCode());
      TransHandler transHandler = manager.getTransHandler(config.getAssetCode());
      List<TransferIn> pendings = transferInService.queryPendings(config.getAssetCode());
      log.info("{} {} transfer in transactions need to query", pendings.size(), config.getAssetCode());
      pendings.forEach(transferIn -> queryTransferIn(transHandler, transferIn));
    } catch (Exception e) {
     log.error("TransferInQueryJob execute error", e);
    }
  }

  private void queryTransferIn(TransHandler transHandler, TransferIn transferIn) {
    try {
      transHandler.queryTransferIn(transferIn);
    } catch (Exception e) {
      log.error("Handle transfer in error, transferIn {}", transferIn, e);
    }
  }
}
