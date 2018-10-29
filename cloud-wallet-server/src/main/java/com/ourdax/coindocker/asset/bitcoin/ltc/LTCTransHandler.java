package com.ourdax.coindocker.asset.bitcoin.ltc;

import com.ourdax.coindocker.asset.bitcoin.BTCBasedDefaultHandler;
import com.ourdax.coindocker.common.enums.AssetCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 9/2/2018
 */
@Lazy
@Component(value = "LTCTransHandler")
public class LTCTransHandler extends BTCBasedDefaultHandler{
  @Autowired
  private LTCProperties properties;

  @Override
  protected Integer getBatchSize() {
    return properties.getBatchSize();
  }

  @Override
  protected Integer getWaitTimeThresholdInMins() {
    return properties.getBatchTimeLimitInMins();
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.LTC;
  }
}
