plugins {
    `java-library`
    id("io.spring.dependency-management")
}

dependencies {
    // 공용 인터페이스/DTO 등 노출이 필요하면 api, 내부만 쓰면 implementation
    api("org.slf4j:slf4j-api:2.0.16")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
}
