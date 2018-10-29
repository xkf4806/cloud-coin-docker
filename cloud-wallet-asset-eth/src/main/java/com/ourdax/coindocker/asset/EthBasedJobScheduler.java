package com.ourdax.coindocker.asset;

import com.ourdax.coindocker.job.DefaultJobScheduler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author think on 3/2/2018
 */
public abstract class EthBasedJobScheduler extends DefaultJobScheduler {

  @Autowired
  private EthBasedJobProperties properties;

  @Override
  protected long getNewTransFindingRate() {
    return properties.getFindNewRate();
  }

  @Override
  protected long getTransferInRate() {
    return properties.getQueryInRate();
  }

  @Override
  protected long getTransferOutQueryRate() {
    return properties.getTransferOutRate();
  }

  @Override
  protected long getTransferOutSendingRate() {
    return properties.getTransferOutRate();
  }
}
