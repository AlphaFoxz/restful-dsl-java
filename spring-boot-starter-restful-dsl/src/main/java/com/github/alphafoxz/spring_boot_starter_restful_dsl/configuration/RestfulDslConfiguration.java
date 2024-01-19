package com.github.alphafoxz.spring_boot_starter_restful_dsl.configuration;

import cn.hutool.core.lang.Snowflake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestfulDslConfiguration {
    @Bean
    public Snowflake snowflake() {
        return new Snowflake(0, 1);
    }
}
