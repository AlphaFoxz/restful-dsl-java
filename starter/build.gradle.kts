var restulDslGroup = "com.github.alphafoxz.restful-dsl-java"
var restulDslVersion = "3.0.0-alpha.3"
tasks.bootJar {
    enabled = false
}
tasks.jar {
    enabled = true
    archiveClassifier.set("")
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