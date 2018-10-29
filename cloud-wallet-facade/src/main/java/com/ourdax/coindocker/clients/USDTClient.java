package com.ourdax.coindocker.clients;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import com.ourdax.coindocker.common.clients.usdt.client.BtcdOmniClient;
import com.ourdax.coindocker.common.enums.AssetCode;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhangjinyang on 2018/6/14.
 */
@Configuration
public class USDTClient implements AssetClient<BtcdOmniClient> {

  @Value("${usdt.rpcHost}")
  private String rpcHost;
  @Value("${usdt.rpcPort}")
  private Integer rpcPort;
  @Value("${usdt.rpcUser}")
  private String rpcUser;
  @Value("${usdt.rpcPassword}")
  private String rpcPassword;

  private volatile BtcdClient omniClient;

  public BtcdClient getClientInstance() {
    if (omniClient == null){
      synchronized (USDTClient.class){
        if (omniClient ==  null){
          omniClient = buildUsdtClient();
        }
      }
    }
    return omniClient;
  }

  protected BtcdClient buildUsdtClient() {
    Properties clientProperties = new Properties();
    clientProperties.setProperty("node.bitcoind.rpc.protocol", "http");
    clientProperties.setProperty("node.bitcoind.rpc.host", rpcHost);
    clientProperties.setProperty("node.bitcoind.rpc.port", String.valueOf(rpcPort));
    clientProperties.setProperty("node.bitcoind.rpc.user", rpcUser);
    clientProperties.setProperty("node.bitcoind.rpc.password", rpcPassword);
    clientProperties.setProperty("node.bitcoind.http.auth_scheme", "Basic");
    try {
      return new BtcdClientImpl(null, clientProperties);
    } catch (Exception e) {
      throw new RuntimeException("Init btcdClient For Usdt error", e);
    }
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.USDT;
  }
}
