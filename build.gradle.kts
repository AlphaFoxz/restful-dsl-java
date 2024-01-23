plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}
group = "com.github.alphafoxz"
version = "3.0.0-alpha.0"
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.bootJar {
    enabled = false
}

allprojects {
    version = "3.0.0-alpha.0"
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    repositories {
        mavenCentral()
    }
    dependencyManagement {
        imports {
            org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES
        }
        dependencies {
            dependency("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
            dependency("cn.hutool:hutool-all:5.8.25")
        }
    }
}

project("spring-boot-starter-restful-dsl") {
    group = "com.github.alphafoxz"
    tasks.bootJar {
        enabled = false
    }
    tasks.jar {
        enabled = true
    }
    dependencies {
        compileOnly("org.springframework.boot:spring-boot-starter-web")
        compileOnly("org.springdoc:springdoc-openapi-starter-webmvc-ui")
        compileOnly("cn.hutool:hutool-all")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    }
}
project("spring-boot-starter-restful-dsl-test") {
    group = "com.github.alphafoxz"
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web") {
            exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
        }
        implementation("org.springframework.boot:spring-boot-starter-undertow")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")
        implementation("cn.hutool:hutool-all")

        implementation(project(":spring-boot-starter-restful-dsl"))
    }
}