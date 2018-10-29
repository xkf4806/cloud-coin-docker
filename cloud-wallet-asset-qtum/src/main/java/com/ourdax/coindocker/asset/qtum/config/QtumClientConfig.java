package com.ourdax.coindocker.asset.qtum.config;

import com.ourdax.coindocker.common.clients.achain.AchainClient;
import com.ourdax.coindocker.common.clients.achain.AchainClientImpl;
import com.ourdax.coindocker.common.clients.qtum.QtumClient;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author think on 5/2/2018
 */
@Configuration
public class QtumClientConfig {

  @Value("${qtum.rpcHost}")
  private String host;

  @Value("${qtum.rpcPort}")
  private String port;

  @Value("${qtum.rpcUser}")
  private String rpcUser;

  @Value("${qtum.rpcPassword}")
  private String rpcPassword;

  @Bean
  @Lazy
  public QtumClient buildQtumClient(String host, Integer port, String user, String password) {
    Properties clientProperties = new Properties();
    clientProperties.setProperty("node.bitcoind.rpc.protocol", "http");
    clientProperties.setProperty("node.bitcoind.rpc.host", host);
    clientProperties.setProperty("node.bitcoind.rpc.port", String.valueOf(port));
    clientProperties.setProperty("node.bitcoind.rpc.user", user);
    clientProperties.setProperty("node.bitcoind.rpc.password", password);
    clientProperties.setProperty("node.bitcoind.http.auth_scheme", "Basic");
    try {
      return new QtumClient(null, clientProperties);
    } catch (Exception e) {
      throw new RuntimeException("Init OmniClient error", e);
    }
  }
}
