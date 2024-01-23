var restulDslGroup = "com.github.alphafoxz.restful-dsl-java"
var restulDslVersion = "3.0.0-alpha.0"
plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("maven-publish")
}
allprojects {
    group = restulDslGroup
    version = restulDslVersion
    repositories {
        mavenCentral()
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
project(":starter") {
    tasks.bootJar {
        enabled = false
//        archiveClassifier.set("")
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
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = restulDslGroup
                artifactId = "spring-boot-starter-restful-dsl"
                version = restulDslVersion
                from(components["java"])
            }
        }
    }
}
project(":test") {
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web") {
            exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
        }
        implementation("org.springframework.boot:spring-boot-starter-undertow")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")
        implementation("cn.hutool:hutool-all")

        implementation(project(":starter"))
    }
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = restulDslGroup
                artifactId = "spring-boot-starter-restful-dsl-test"
                version = restulDslVersion
                from(components["java"])
            }
        }
    }
}
