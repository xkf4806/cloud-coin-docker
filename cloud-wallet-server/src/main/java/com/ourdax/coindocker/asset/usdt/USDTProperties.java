package com.ourdax.coindocker.asset.usdt;

import com.ourdax.coindocker.asset.bitcoin.BTCBasedProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author think on 9/2/2018
 */
@Configuration
@ConfigurationProperties(prefix = "usdt")
public class USDTProperties extends USDTBasedProperties {}
