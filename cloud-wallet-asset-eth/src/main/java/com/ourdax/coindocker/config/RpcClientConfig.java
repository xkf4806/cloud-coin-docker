package com.ourdax.coindocker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;

/**
 * @author think on 3/2/2018
 */
@Configuration
public class RpcClientConfig {

  @Value("${eth.rpcServiceUrl}")
  private String rpcServiceUrl;

  @Bean
  public Admin ethRpcClient() {
    return Admin.build(new HttpService(rpcServiceUrl));
  }

}
