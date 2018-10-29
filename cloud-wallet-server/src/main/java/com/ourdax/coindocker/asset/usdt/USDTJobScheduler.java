package com.ourdax.coindocker.asset.usdt;

import com.ourdax.coindocker.asset.bitcoin.BTCBasedJobScheduler;
import com.ourdax.coindocker.common.enums.AssetCode;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 9/2/2018
 */
@Component
@Lazy
public class USDTJobScheduler extends USDTBasedJobScheduler {

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.USDT;
  }
}
