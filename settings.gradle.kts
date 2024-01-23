pluginManagement {
    val springbootPluginVersion: String by settings
    val springDependencyManagementPluginVersion: String by settings
    plugins {
        id("org.springframework.boot") version springbootPluginVersion
        id("io.spring.dependency-management") version springDependencyManagementPluginVersion
    }
}
rootProject.name = "spring-boot-starter-restful-dsl-project"

include(
    ":spring-boot-starter-restful-dsl-test",
    ":spring-boot-starter-restful-dsl",
)

