package com.ourdax.coindocker.asset.bitcoin.btc;

import com.ourdax.coindocker.asset.bitcoin.BTCBasedDefaultHandler;
import com.ourdax.coindocker.common.enums.AssetCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 9/2/2018
 */
@Lazy
@Component(value = "BTCTransHandler")
@Slf4j
public class BTCTransHandler extends BTCBasedDefaultHandler {

  @Autowired
  private BTCProperties properties;


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
    return AssetCode.BTC;
  }
}
