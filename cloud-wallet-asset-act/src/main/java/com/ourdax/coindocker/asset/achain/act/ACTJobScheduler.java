package com.ourdax.coindocker.asset.achain.act;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.job.DefaultJobScheduler;
import org.springframework.stereotype.Component;

/**
 * @author think on 3/2/2018
 */
@Component
public class ACTJobScheduler extends DefaultJobScheduler {

  private static final long BLOCK_GENERATE_RATE = 5;

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
    return 20;
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.ACT;
  }
}
