package com.ourdax.coindocker.asset.bitcoin.btc;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.ourdax.coindocker.asset.bitcoin.BTCBasedRpcProcessor;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 9/2/2018
 */
@Lazy
@Component(value = "BTCRpcProcessor")
public class BTCRpcProcessor extends BTCBasedRpcProcessor {

  @Autowired
  private BTCProperties properties;

  private BtcdClient client;

  @PostConstruct
  public void init() {
    client = buildClient(properties.getRpcHost(), properties.getRpcPort(),
        properties.getRpcUser(), properties.getRpcPassword());
  }

  @Override
  protected BtcdClient getClient() {
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
    return AssetCode.BTC;
  }

  @Override
  public String getUniformAccount() {
    return getDepositAccount();
  }

}
