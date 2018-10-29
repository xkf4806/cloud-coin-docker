package com.ourdax.coindocker.asset.bitcoin.bch;

import com.ourdax.coindocker.asset.bitcoin.BTCBasedProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author think on 22/1/2018
 */
@Configuration
@ConfigurationProperties(prefix = "bch")
public class BCHProperties extends BTCBasedProperties {}
