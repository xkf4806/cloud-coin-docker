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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author think on 11/1/2018
 */
@Component
@Slf4j
public class TransferOutQueryJob extends AbstractJob {

  @Autowired
  private TransferOutService transferOutService;

  @Autowired
  private AssetComponentManager manager;

  @Override
  public String getName() {
    return TransferOutQueryJob.class.getSimpleName();
  }

  public void run(JobConfig config) {
    try {
      AssetCode assetCode = config.getAssetCode();
      log.info("TransferOutQueryJob querying {} transfer out transaction status...", assetCode);
      TransHandler transHandler = manager.getTransHandler(assetCode);
      List<TransferOut> pendings = transferOutService.queryPendings(assetCode);
      log.info("{} {} transfer out transactions need to query", pendings.size(), assetCode);
      pendings.stream()
          .filter(transferOut -> StringUtils.isNotEmpty(transferOut.getTxId()))
          .forEach(transferOut -> queryTransferOut(transHandler, transferOut));
    } catch (Exception e) {
      log.error("TransferOutQueryJob execute error", e);
    }
  }

  private void queryTransferOut(TransHandler transHandler, TransferOut transferOut) {
    try {
      log.info("Querying transfer out result, req: {}", transferOut);
      transHandler.queryTransferOut(transferOut);
    } catch (Exception e) {
      log.error("Handle transfer out error, transferOut: {}", transferOut, e);
    }
  }
}
