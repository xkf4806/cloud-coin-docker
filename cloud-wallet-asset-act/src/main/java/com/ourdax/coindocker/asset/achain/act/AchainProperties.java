package com.ourdax.coindocker.asset.achain.act;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author think on 15/1/2018
 */
@Configuration
@ConfigurationProperties(prefix = "act")
@Data
public class AchainProperties {
  private Integer decimals;
  private String walletName;
  private String walletPassword;
  private String accountName;
  private String accountAddress;
}
