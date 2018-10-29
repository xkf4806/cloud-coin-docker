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
public class DOGEClient implements AssetClient<BtcdClient> {

  @Value("${doge.rpcHost}")
  private String rpcHost;
  @Value("${doge.rpcPort}")
  private Integer rpcPort;
  @Value("${doge.rpcUser}")
  private String rpcUser;
  @Value("${doge.rpcPassword}")
  private String rpcPassword;

  private volatile BtcdClient btcdClient;

  public BtcdClient getClientInstance() {
    if (btcdClient == null){
      synchronized (DOGEClient.class){
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
      throw new RuntimeException("Init BtcdClient for DOGE Client error", e);
    }
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.DOGE;
  }
}
