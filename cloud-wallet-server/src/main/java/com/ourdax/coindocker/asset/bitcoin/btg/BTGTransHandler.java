package com.ourdax.coindocker.asset.bitcoin.btg;

import com.ourdax.coindocker.asset.bitcoin.BTCBasedDefaultHandler;
import com.ourdax.coindocker.common.enums.AssetCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author xj.x
 * @date 2018-10-17 13:26
 */
@Lazy
@Component(value = "BTGTransHandler")
@Slf4j
public class BTGTransHandler extends BTCBasedDefaultHandler {

  @Autowired
  private BTGProperties properties;


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
    return AssetCode.BTG;
  }
}
