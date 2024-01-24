var restulDslGroup = "com.github.alphafoxz.restful-dsl-java"
var restulDslVersion = "3.0.0-alpha.0"
tasks.bootJar {
    enabled = true
}
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