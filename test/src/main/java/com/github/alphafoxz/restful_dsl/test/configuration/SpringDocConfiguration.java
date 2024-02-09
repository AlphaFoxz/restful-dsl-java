package com.github.alphafoxz.restful_dsl.test.configuration;

import cn.hutool.core.collection.CollUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@EnableOpenApi
@Configuration
public class SpringDocConfiguration {
    @Bean
    public Docket restfulApi() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("restful_dsl-starter")
                .apiInfo(getInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.alphafoxz.restful_dsl.starter"))
//                .paths(PathSelectors.regex("/_restfulDsl/*+"))
                .build();
    }

    @Bean
    public Docket testApi() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("restful_dsl-test")
                .apiInfo(getInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.alphafoxz.restful_dsl.test"))
//                .paths(PathSelectors.regex("/test/*"))
                .build();
    }

    private ApiInfo getInfo() {
        ApiInfoBuilder builder = new ApiInfoBuilder().title("RESTful DSL 测试 API文档")
                .description("基于SpringDoc生成的API文档")
                .version("0.0.1-alpha.0")
                .licenseUrl("https://github.com/AlphaFoxz/restful-dsl-java/blob/main/LICENSE");
        Contact contact = new Contact(
                "AlphaFoxz",
                "https://github.com/AlphaFoxz",
                "841958335@qq.com"
        );
        builder.contact(contact);
        builder.extensions(CollUtil.newArrayList(new StringVendorExtension("项目地址（java）", "https://github.com/AlphaFoxz/restful-dsl-java")));
        return builder.build();
    }
}
