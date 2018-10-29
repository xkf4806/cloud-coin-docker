package com.ourdax.coindocker.asset.bitcoin.bch;

import com.ourdax.coindocker.asset.bitcoin.BTCBasedJobScheduler;
import com.ourdax.coindocker.common.enums.AssetCode;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 3/2/2018
 */
@Component
@Lazy
public class BCHJobScheduler extends BTCBasedJobScheduler {

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.BCH;
  }
}
