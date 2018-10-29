package com.ourdax.coindocker.asset.usdt;

import com.ourdax.coindocker.common.clients.usdt.client.BtcdOmniClient;
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
@Component(value = "USDTRpcProcessor")
public class USDTRpcProcessor extends USDTBasedRpcProcessor {

  @Autowired
  private USDTProperties properties;

  private BtcdOmniClient client;

  @PostConstruct
  public void init() {
    client = buildClient(properties.getRpcHost(), properties.getRpcPort(),
        properties.getRpcUser(), properties.getRpcPassword());
  }

  @Override
  protected BtcdOmniClient getClient() {
    return client;
  }

  @Override
  protected String getDepositAddress() {
    return properties.getDepositAddress();
  }

  @Override
  protected Integer getConfirmThreshold() {
    return properties.getConfirmThreshold();
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.USDT;
  }

  @Override
  public String getUniformAccount() {
    return getDepositAddress();
  }

  @Override
  protected String getFeeAddress() {
    return properties.getFeeAddress();
  }

}
