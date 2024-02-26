package com.github.alphafoxz.restful_dsl.starter;

import cn.hutool.core.lang.Snowflake;
import com.github.alphafoxz.restful_dsl.starter.configuration.RestfulDslProperties;
import com.github.alphafoxz.restful_dsl.starter.service.RestfulDslInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@ComponentScan(value = {
        "com.github.alphafoxz.restful_dsl.starter.configuration",
        "com.github.alphafoxz.restful_dsl.starter.controller",
        "com.github.alphafoxz.restful_dsl.starter.service",
})
public class RestfulDslAutoConfiguration {
    @Resource
    private RestfulDslInfoService restfulDslInfoService;

    @Bean
    @ConditionalOnMissingBean(Snowflake.class)
    public Snowflake snowflake() {
        return new Snowflake(0, 1);
    }

    @Autowired
    public void init(RestfulDslProperties restfulDslProperties) {
        restfulDslInfoService.checkErr();
        log.info("restful-dsl服务启动！\n目标项目：{}\n包含模块：{}", restfulDslProperties.getBasePackage(), restfulDslProperties.getIncludeModules());
    }
}
