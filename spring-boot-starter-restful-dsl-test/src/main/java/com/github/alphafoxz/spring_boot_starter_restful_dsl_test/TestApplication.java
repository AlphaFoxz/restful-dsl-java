package com.github.alphafoxz.spring_boot_starter_restful_dsl_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = "com.github.alphafoxz.spring_boot_starter_restful_dsl_test"
)
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
