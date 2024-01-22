package com.github.alphafoxz.spring_boot_starter_restful_dsl;

import com.github.alphafoxz.spring_boot_starter_restful_dsl.configuration.RestfulDslProperties;
import com.github.alphafoxz.spring_boot_starter_restful_dsl.service.RestfulDslInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@ComponentScan(value = {
        "com.github.alphafoxz.spring_boot_starter_restful_dsl.configuration",
        "com.github.alphafoxz.spring_boot_starter_restful_dsl.controller",
        "com.github.alphafoxz.spring_boot_starter_restful_dsl.service",
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
