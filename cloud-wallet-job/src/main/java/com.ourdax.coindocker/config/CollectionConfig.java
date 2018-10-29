package com.ourdax.coindocker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhangjinyang on 2018/7/20.
 */
@Configuration
@ConfigurationProperties(prefix = "collect")
@Data
public class CollectionConfig {

  private String ethFeeBasic;

  private String btcFeeBasic;

}
