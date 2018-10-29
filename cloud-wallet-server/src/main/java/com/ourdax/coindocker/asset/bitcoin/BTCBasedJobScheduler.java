package com.ourdax.coindocker.asset.bitcoin;

import com.ourdax.coindocker.job.DefaultJobScheduler;

/**
 * @author think on 27/2/2018
 */
public abstract class BTCBasedJobScheduler extends DefaultJobScheduler {

  private static final long BLOCK_GENERATE_RATE = 120;

  @Override
  protected long getNewTransFindingRate() {
    return BLOCK_GENERATE_RATE;
  }

  @Override
  protected long getTransferInRate() {
    return BLOCK_GENERATE_RATE;
  }

  @Override
  protected long getTransferOutQueryRate() {
    return BLOCK_GENERATE_RATE;
  }

  @Override
  protected long getTransferOutSendingRate() {
    return 30;
  }
}
