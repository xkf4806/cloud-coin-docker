package com.ourdax.coindocker.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.ourdax.coindocker.dao")
public class MyBatisConfig {
}