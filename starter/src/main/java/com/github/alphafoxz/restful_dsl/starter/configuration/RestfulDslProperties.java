package com.github.alphafoxz.restful_dsl.starter.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "dsl.restl")
public class RestfulDslProperties {
    private GenDtoFields genDtoFields = new GenDtoFields();
    private String basePackage = "com.github.alphafoxz";
    private List<String> includeModules;
    private String codePackage = "gen.restl";
    private String httpControllerClass = "HttpController";
    private String pageClass = "org.springframework.data.domain.Page";

    @Data
    public static class GenDtoFields {
        private Boolean enabled = false;
        private String packageName = "_compile_only";
    }
}
