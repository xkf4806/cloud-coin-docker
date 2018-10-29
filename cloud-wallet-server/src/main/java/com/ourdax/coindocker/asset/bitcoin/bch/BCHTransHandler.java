package com.ourdax.coindocker.asset.bitcoin.bch;

import com.ourdax.coindocker.asset.bitcoin.BTCBasedDefaultHandler;
import com.ourdax.coindocker.common.enums.AssetCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 22/1/2018
 */
@Lazy
@Component(value = "BCHTransHandler")
@Slf4j
public class BCHTransHandler extends BTCBasedDefaultHandler {

  @Autowired
  private BCHProperties properties;


  @Override
  public AssetCode getAssetCode() {
    return AssetCode.BCH;
  }


  @Override
  protected Integer getBatchSize() {
    return properties.getBatchSize();
  }

  @Override
  protected Integer getWaitTimeThresholdInMins() {
    return properties.getBatchTimeLimitInMins();
  }
}
