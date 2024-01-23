package com.github.alphafoxz.restful_dsl.starter;

import com.github.alphafoxz.restful_dsl.starter.configuration.RestfulDslProperties;
import com.github.alphafoxz.restful_dsl.starter.service.RestfulDslInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

@Slf4j
@ComponentScan(value = {
        "com.github.alphafoxz.restful_dsl.starter.configuration",
        "com.github.alphafoxz.restful_dsl.starter.controller",
        "com.github.alphafoxz.restful_dsl.starter.service",
})
public class RestfulDslAutoConfiguration {
    @Resource
    private RestfulDslInfoService restfulDslInfoService;

    @Autowired
    public void init(RestfulDslProperties restfulDslProperties) {
        restfulDslInfoService.checkErr();
        log.info("restful-dsl服务启动！\n目标项目：{}\n包含模块：{}", restfulDslProperties.getBasePackage(), restfulDslProperties.getIncludeModules());
    }
}
