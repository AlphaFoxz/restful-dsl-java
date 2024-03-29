package com.github.alphafoxz.restful_dsl.starter.configuration;

import cn.hutool.core.lang.Snowflake;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestfulDslConfiguration {
    @Bean
    @ConditionalOnMissingBean(Snowflake.class)
    public Snowflake snowflake() {
        return new Snowflake(0, 1);
    }
}
