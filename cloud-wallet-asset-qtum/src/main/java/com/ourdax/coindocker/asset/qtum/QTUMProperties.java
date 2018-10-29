package com.ourdax.coindocker.asset.qtum;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author think on 9/2/2018
 */
@Configuration
@ConfigurationProperties(prefix = "qtum")
public class QTUMProperties extends QTUMBasedProperties { }
