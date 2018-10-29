package com.ourdax.coindocker.asset.qtum.qrc20.ink;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.job.DefaultJobScheduler;
import org.springframework.stereotype.Component;

/**
 * @author think on 3/2/2018
 */
@Component
public class INKJobScheduler extends DefaultJobScheduler {

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.INK;
  }
}
