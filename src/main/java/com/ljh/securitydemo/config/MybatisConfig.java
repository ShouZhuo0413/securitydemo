package com.ljh.securitydemo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"com.ljh.securitydemo.mapper"})
public class MybatisConfig {
}
