package com.github.alphafoxz.spring_boot_starter_restful_dsl.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "dsl.restful")
public class RestfulDslProperties {
    private String basePackage = "com.github.alphafoxz";
    private List<String> includeModules;
    private String codePackage = "gen.restful";
    private String httpControllerClass = "HttpController";
    private String pageClass = "org.springframework.data.domain.Page";
}
