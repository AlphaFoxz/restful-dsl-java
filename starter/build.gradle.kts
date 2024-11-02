var restulDslGroup = "com.github.alphafoxz.restful-dsl-java"
var restulDslVersion = "0.0.1-alpha.0"
tasks.bootJar {
    enabled = false
}
dependencies {
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springdoc:springdoc-openapi-starter-webmvc-ui")
    compileOnly("cn.hutool:hutool-core")
    compileOnly("cn.hutool:hutool-crypto")
    compileOnly("cn.hutool:hutool-json")
    compileOnly("cn.hutool:hutool-extra")
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