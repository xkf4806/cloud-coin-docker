package com.ourdax.coindocker.clients;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import com.ourdax.coindocker.common.clients.achain.AchainClient;
import com.ourdax.coindocker.common.clients.achain.AchainClientImpl;
import com.ourdax.coindocker.common.enums.AssetCode;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhangjinyang on 2018/6/14.
 */
@Configuration
public class ACTClient implements AssetClient<AchainClient> {

  @Value("${achain.rpc.host}")
  private String rpcHost;
  @Value("${achain.rpc.port}")
  private Integer rpcPort;
  @Value("${achain.rpc.user}")
  private String rpcUser;
  @Value("${achain.rpc.password}")
  private String rpcPassword;

  private volatile AchainClient achainClient;

  public AchainClient getClientInstance() {
    if (achainClient == null){
      synchronized (ACTClient.class){
        if (achainClient ==  null){
          achainClient = buildAchainClient();
        }
      }
    }
    return achainClient;
  }

  public AchainClient buildAchainClient() {
    Properties clientConfig = new Properties();
    clientConfig.setProperty("achain.rpc.protocol", "http");
    clientConfig.setProperty("achain.rpc.host", rpcHost);
    clientConfig.setProperty("achain.rpc.port", rpcPort.toString());
    clientConfig.setProperty("achain.rpc.user", rpcUser);
    clientConfig.setProperty("achain.rpc.password", rpcPassword);
    clientConfig.setProperty("achain.http.auth_scheme", "Basic");
    return new AchainClientImpl(clientConfig);
  }

  @Override
  public AssetCode getAssetCode() {
    return AssetCode.ACT;
  }
}
