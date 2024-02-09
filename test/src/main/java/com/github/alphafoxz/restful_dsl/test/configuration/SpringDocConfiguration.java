package com.github.alphafoxz.restful_dsl.test.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {
    @Bean
    public GroupedOpenApi restfulApi() {
        return GroupedOpenApi.builder()
                .group("restful_dsl-starter")
                .packagesToScan("com.github.alphafoxz.restful_dsl.starter")
//                .pathsToMatch("/_restfulDsl/**")
                .build();
    }

    @Bean
    public GroupedOpenApi testApi() {
        return GroupedOpenApi.builder()
                .group("restful_dsl-test")
                .packagesToScan("com.github.alphafoxz.restful_dsl.test")
                .build();
    }

    @Bean
    public OpenAPI indexApi() {
        return new OpenAPI()
                .info(getInfo())
                .externalDocs(new ExternalDocumentation()
                        .description("SpringDoc Documentation")
                        .url("https://springdoc.org/"));
    }

    private Info getInfo() {
        Info info = new Info().title("RESTful DSL 测试 API文档")
                .description("基于SpringDoc生成的API文档")
                .version("0.1.0-alpha.0")
                .license(new License().name("Apache 2.0").url("https://github.com/AlphaFoxz/restful-dsl-java/blob/main/LICENSE"));
        info.addExtension("sdk项目地址（java）", "https://github.com/AlphaFoxz/restful-dsl-java");
        Contact contact = new Contact();
        contact.setName("AlphaFoxz");
        contact.setEmail("841958335@qq.com");
        contact.setUrl("https://github.com/AlphaFoxz");
        info.contact(contact);
        return info;
    }
}
