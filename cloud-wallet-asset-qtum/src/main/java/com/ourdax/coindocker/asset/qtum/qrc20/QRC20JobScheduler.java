package com.ourdax.coindocker.asset.qtum.qrc20;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.job.DefaultJobScheduler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 9/2/2018
 */
@Component
@Lazy
public class QRC20JobScheduler extends DefaultJobScheduler {

  private static final long BLOCK_GENERATE_RATE = 10;

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

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.QRC20;
  }
}
