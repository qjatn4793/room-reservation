plugins {
    `java-library`
    id("io.spring.dependency-management")
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.14.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
}
