var restulDslGroup = "com.github.alphafoxz.restful-dsl-java"
var restulDslVersion = "3.0.0-alpha.4"
plugins {
    id("java-library")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("maven-publish")
}
tasks.bootJar {
    enabled = false
}
tasks.jar {
    enabled = false
}

allprojects {
    apply(plugin = "java-library")
    group = restulDslGroup
    version = restulDslVersion
    repositories {
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "maven-publish")
    tasks.jar {
        enabled = true
        archiveClassifier.set("")
//        exclude("**/_compile_only/**")
    }
    dependencyManagement {
        imports {
            org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES
        }
        dependencies {
            dependency("com.google.code.findbugs:jsr305:3.0.2")
            dependency("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
            dependency("cn.hutool:hutool-core:5.8.25")
            dependency("cn.hutool:hutool-crypto:5.8.25")
            dependency("cn.hutool:hutool-json:5.8.25")
            dependency("cn.hutool:hutool-extra:5.8.25")
            dependency("cn.hutool:hutool-all:5.8.25")
        }
    }
}
