var restulDslGroup = "com.github.alphafoxz.restful-dsl-java"
var restulDslVersion = "3.0.0-alpha.3"
plugins {
    id("java")
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
    group = restulDslGroup
    version = restulDslVersion
    repositories {
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}
subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "maven-publish")
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    tasks.jar {
        enabled = true
        archiveClassifier.set("")
    }
    dependencyManagement {
        imports {
            org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES
        }
        dependencies {
            dependency("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
            dependency("cn.hutool:hutool-core:5.8.25")
            dependency("cn.hutool:hutool-crypto:5.8.25")
            dependency("cn.hutool:hutool-json:5.8.25")
            dependency("cn.hutool:hutool-extra:5.8.25")
            dependency("cn.hutool:hutool-all:5.8.25")
        }
    }
}
