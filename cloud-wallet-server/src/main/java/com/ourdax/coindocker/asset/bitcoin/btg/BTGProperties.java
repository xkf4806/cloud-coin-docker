package com.ourdax.coindocker.asset.bitcoin.btg;

import com.ourdax.coindocker.asset.bitcoin.BTCBasedProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author xj.x
 * @date 2018-10-17 13:28
 */
@Configuration
@ConfigurationProperties(prefix = "btg")
public class BTGProperties extends BTCBasedProperties { }
