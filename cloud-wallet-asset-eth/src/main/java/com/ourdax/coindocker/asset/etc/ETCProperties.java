package com.ourdax.coindocker.asset.etc;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhangjinyang on 2018/1/22.
 */
@Configuration
@ConfigurationProperties(prefix = "etc")
@Data
public class ETCProperties {

  @Value("${etc.serviceUrl}")
  private String serviceUrl;

  @Value("${etc.depositAddress}")
  private String depositAddress;

  @Value("${etc.passphrase}")
  private String passphrase;

  @Value("${etc.confirmThreshold}")
  private Integer confirmThreshold;

  @Value("${etc.decimals}")
  private Integer decimals;
}
