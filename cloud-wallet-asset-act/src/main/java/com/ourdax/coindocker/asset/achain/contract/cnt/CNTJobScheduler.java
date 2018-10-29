package com.ourdax.coindocker.asset.achain.contract.cnt;

import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.job.DefaultJobScheduler;
import org.springframework.stereotype.Component;

/**
 * @author think on 3/2/2018
 */
@Component
public class CNTJobScheduler extends DefaultJobScheduler {

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.CNT;
  }
}
