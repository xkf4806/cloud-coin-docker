package com.ourdax.coindocker.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by hongzong.li on 11/8/16.
 */
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {
  @ConfigurationProperties(prefix = "hikaricp")
  @Bean
  public DataSource dataSource() {
    return new HikariDataSource();
  }

  @Bean
  public DataSourceTransactionManager transactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }
}
