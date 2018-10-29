package com.ourdax.coindocker.clients;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import com.ourdax.coindocker.common.clients.qtum.QtumClient;
import com.ourdax.coindocker.common.enums.AssetCode;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhangjinyang on 2018/6/14.
 */
@Configuration
public class QTUMClient implements AssetClient<QtumClient> {

  @Value("${qtum.rpcHost}")
  private String rpcHost;
  @Value("${qtum.rpcPort}")
  private Integer rpcPort;
  @Value("${qtum.rpcUser}")
  private String rpcUser;
  @Value("${qtum.rpcPassword}")
  private String rpcPassword;

  private volatile QtumClient qtumClient;

  public QtumClient getClientInstance() {
    if (qtumClient == null){
      synchronized (QTUMClient.class){
        if (qtumClient ==  null){
          qtumClient = buildQtumClient();
        }
      }
    }
    return qtumClient;
  }

  private QtumClient buildQtumClient() {
    Properties clientProperties = new Properties();
    clientProperties.setProperty("node.bitcoind.rpc.protocol", "http");
    clientProperties.setProperty("node.bitcoind.rpc.host", rpcHost);
    clientProperties.setProperty("node.bitcoind.rpc.port", String.valueOf(rpcPort));
    clientProperties.setProperty("node.bitcoind.rpc.user", rpcUser);
    clientProperties.setProperty("node.bitcoind.rpc.password", rpcPassword);
    clientProperties.setProperty("node.bitcoind.http.auth_scheme", "Basic");
    try {
      return new QtumClient(null, clientProperties);
    } catch (Exception e) {
      throw new RuntimeException("Init BtcdClient For QTUM Client error", e);
    }
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.QTUM;
  }
}
