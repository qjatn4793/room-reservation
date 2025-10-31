plugins {
    `java-library`
    kotlin("jvm") version "1.9.25"
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "17"
}