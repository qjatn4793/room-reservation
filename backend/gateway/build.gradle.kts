plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    java
}
dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
springBoot { mainClass.set("com.rr.gateway.GatewayApplication") }
tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun>().configureEach {
    mainClass.set("com.example.gateway.GatewayApplication")
}
