package com.ourdax.coindocker.asset.achain;

import com.ourdax.coindocker.common.clients.achain.AchainClient;
import com.ourdax.coindocker.common.clients.achain.AchainClientImpl;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author think on 5/2/2018
 */
@Configuration
public class AchainClientConfig {

  @Value("${achain.rpc.host}")
  private String host;

  @Value("${achain.rpc.port}")
  private String port;

  @Value("${achain.rpc.user}")
  private String rpcUser;

  @Value("${achain.rpc.password}")
  private String rpcPassword;

  @Bean
  @Lazy
  public AchainClient achainClient() {
    Properties clientConfig = new Properties();
    clientConfig.setProperty("achain.rpc.protocol", "http");
    clientConfig.setProperty("achain.rpc.host", host);
    clientConfig.setProperty("achain.rpc.port", port);
    clientConfig.setProperty("achain.rpc.user", rpcUser);
    clientConfig.setProperty("achain.rpc.password", rpcPassword);
    clientConfig.setProperty("achain.http.auth_scheme", "Basic");
    return new AchainClientImpl(clientConfig);
  }
}
