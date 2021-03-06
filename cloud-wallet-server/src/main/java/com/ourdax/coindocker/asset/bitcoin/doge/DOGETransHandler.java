package com.ourdax.coindocker.asset.bitcoin.doge;

import com.ourdax.coindocker.asset.bitcoin.BTCBasedDefaultHandler;
import com.ourdax.coindocker.common.enums.AssetCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 9/2/2018
 */
@Lazy
@Component(value = "DOGETransHandler")
public class DOGETransHandler extends BTCBasedDefaultHandler{
  @Autowired
  private DOGEProperties properties;

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
    return AssetCode.DOGE;
  }
}
