package com.ourdax.coindocker.asset.bitcoin.bch;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.ourdax.coindocker.asset.bitcoin.BTCBasedRpcProcessor;
import com.ourdax.coindocker.common.enums.AssetCode;
import com.ourdax.coindocker.rpc.RpcProcessor;
import com.ourdax.coindocker.rpc.RpcTransRequest;
import com.ourdax.coindocker.rpc.RpcTransResponse;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author think on 22/1/2018
 */
@Lazy
@Component(value = "BCHRpcProcessor")
public class BCHRpcProcessor extends BTCBasedRpcProcessor implements RpcProcessor {

  private BtcdClient client;

  @Autowired
  private BCHProperties properties;


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
    return AssetCode.BCH;
  }

  @Override
  public String getUniformAccount() {
    return getDepositAccount();
  }

}
