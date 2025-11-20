plugins {
    id("org.springframework.boot") version "3.2.9"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("kapt") version "1.9.25"
}

dependencies {
    implementation(project(":common"))
    implementation(project(":util"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // QueryDSL -> OpenFeign Querydsl (패치된 라인 사용)
    val qdsl = "6.10.1" // 또는 최신 7.x(스프링/하이버네이트 호환 확인 후)
    implementation("io.github.openfeign.querydsl:querydsl-core:$qdsl")
    implementation("io.github.openfeign.querydsl:querydsl-jpa:$qdsl")
    kapt("io.github.openfeign.querydsl:querydsl-apt:$qdsl:jakarta")

    // PostgreSQL JDBC 보안버전 고정 (CVE-2024-1597 대응)
    runtimeOnly("org.postgresql:postgresql:42.7.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}