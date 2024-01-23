pluginManagement {
    val springbootPluginVersion: String by settings
    val springDependencyManagementPluginVersion: String by settings
    plugins {
        id("org.springframework.boot") version springbootPluginVersion
        id("io.spring.dependency-management") version springDependencyManagementPluginVersion
    }
}

include(
    ":test",
    ":starter",
)

