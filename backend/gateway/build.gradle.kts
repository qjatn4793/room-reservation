plugins {
    id("org.springframework.boot") version "3.2.9"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
}

dependencies {
    implementation(project(":common"))

    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // mac os issue
    implementation("io.netty:netty-resolver-dns-native-macos:4.1.109.Final:osx-aarch_64")
    implementation("io.projectreactor.netty:reactor-netty-core:1.1.17")
    implementation("io.projectreactor.netty:reactor-netty-http:1.1.17")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")
    }
}