package com.ourdax.coindocker.asset.bitcoin.btg;

import com.ourdax.coindocker.common.clients.btg.BTGClient;
import com.ourdax.coindocker.common.enums.AssetCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author xj.x
 * @date 2018-10-17 13:28
 */
@Lazy
@Component(value = "BTGRpcProcessor")
public class BTGRpcProcessor extends BTGBasedRpcProcessor {

  @Autowired
  private BTGProperties properties;

  private BTGClient client;

  @PostConstruct
  public void init() {
    client = buildClient(properties.getRpcHost(), properties.getRpcPort(),
        properties.getRpcUser(), properties.getRpcPassword());
  }

  @Override
  protected BTGClient getClient() {
    return client;
  }

  @Override
  protected String getDepositAccount() {
    return properties.getDepositAccount();
  }

  @Override
  protected Integer getConfirmThreshold() {
    return properties.getConfirmThreshold();
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.BTG;
  }
}
