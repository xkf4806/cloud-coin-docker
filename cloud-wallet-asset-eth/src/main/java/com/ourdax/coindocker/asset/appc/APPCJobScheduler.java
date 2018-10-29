package com.ourdax.coindocker.asset.appc;

import com.ourdax.coindocker.asset.EthBasedJobScheduler;
import com.ourdax.coindocker.common.enums.AssetCode;
import org.springframework.stereotype.Component;

/**
 * @author think on 3/2/2018
 */
@Component
public class APPCJobScheduler extends EthBasedJobScheduler {

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.APPC;
  }
}
