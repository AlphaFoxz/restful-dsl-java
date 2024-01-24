package com.github.alphafoxz.restful_dsl.test.configuration;

import cn.hutool.core.lang.Snowflake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowFlakeConfiguration {
    @Bean
    public Snowflake snowflake() {
        return new Snowflake(0, 1);
    }
}
