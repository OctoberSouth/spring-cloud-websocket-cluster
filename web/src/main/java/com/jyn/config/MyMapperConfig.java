package com.jyn.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 10263
 * EnableTransactionManagement 事务
 */
@EnableTransactionManagement
@Configuration
@MapperScan(basePackages = "com.jyn.web.dao")
public class MyMapperConfig {
}
