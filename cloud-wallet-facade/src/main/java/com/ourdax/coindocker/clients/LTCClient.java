package com.ourdax.coindocker.clients;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import com.ourdax.coindocker.common.enums.AssetCode;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhangjinyang on 2018/6/14.
 */
@Configuration
public class LTCClient implements AssetClient<BtcdClient> {

  @Value("${ltc.rpcHost}")
  private String rpcHost;
  @Value("${ltc.rpcPort}")
  private Integer rpcPort;
  @Value("${ltc.rpcUser}")
  private String rpcUser;
  @Value("${ltc.rpcPassword}")
  private String rpcPassword;

  private volatile BtcdClient btcdClient;

  public BtcdClient getClientInstance() {
    if (btcdClient == null){
      synchronized (LTCClient.class){
        if (btcdClient ==  null){
          btcdClient = buildBtcdClient();
        }
      }
    }
    return btcdClient;
  }

  private BtcdClient buildBtcdClient() {
    Properties clientProperties = new Properties();
    clientProperties.setProperty("node.bitcoind.rpc.protocol", "http");
    clientProperties.setProperty("node.bitcoind.rpc.host", rpcHost);
    clientProperties.setProperty("node.bitcoind.rpc.port", String.valueOf(rpcPort));
    clientProperties.setProperty("node.bitcoind.rpc.user", rpcUser);
    clientProperties.setProperty("node.bitcoind.rpc.password", rpcPassword);
    clientProperties.setProperty("node.bitcoind.http.auth_scheme", "Basic");
    try {
      return new BtcdClientImpl(clientProperties);
    } catch (Exception e) {
      throw new RuntimeException("Init BtcdClient for LTC Client error", e);
    }
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.LTC;
  }
}
