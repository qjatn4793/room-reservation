// backend/build.gradle.kts  (루트)

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension

plugins {
    id("org.springframework.boot") version "3.3.4" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
}

subprojects {
    repositories { mavenCentral() }

    // 자바 플러그인이 '실제로 적용된 모듈'에만 toolchain 설정
    plugins.withType<JavaPlugin> {
        the<JavaPluginExtension>().toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
    // (부트는 내부적으로 java 플러그인을 적용하므로 위 한 줄이면 충분)

    // dependency-management 를 적용한 모듈에만 BOM 주입
    plugins.withId("io.spring.dependency-management") {
        the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
            imports {
                mavenBom("org.springframework.boot:spring-boot-dependencies:3.3.4")
                mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")
            }
        }
    }

    tasks.withType<Test> { useJUnitPlatform() }
}

tasks.withType<Wrapper>().configureEach {
    gradleVersion = "8.14"
    distributionType = Wrapper.DistributionType.BIN
}