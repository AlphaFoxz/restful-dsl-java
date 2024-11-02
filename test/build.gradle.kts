var restulDslGroup = "com.github.alphafoxz.restful-dsl-java"
var restulDslVersion = "0.0.1-alpha.0"
tasks.bootJar {
    enabled = true
}
dependencies {
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")
    implementation("cn.hutool:hutool-all")

    implementation(project(":starter"))
//    implementation("com.github.AlphaFoxz.restful-dsl-java:spring-boot-starter-restful-dsl:v3.0.0-alpha.3")
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