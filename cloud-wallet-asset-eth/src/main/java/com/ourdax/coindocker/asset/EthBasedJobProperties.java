package com.ourdax.coindocker.asset;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author think on 3/2/2018
 */
@Configuration
@ConfigurationProperties(prefix = "job")
@Data
public class EthBasedJobProperties {
  private Long findNewRate;
  private Long queryInRate;
  private Long queryOutRate;
  private Long transferOutRate;
}
