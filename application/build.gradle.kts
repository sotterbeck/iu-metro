plugins {
    id("java-library")
}

group = "de.sotterbeck"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.2")
    implementation(libs.jackson.databind)
    implementation(libs.jackson.datatype.jdk8)
    implementation(libs.java.jwt)
    compileOnly("org.jetbrains:annotations:24.0.1")
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platform.launcher)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
}

tasks {
    test {
        useJUnitPlatform()
    }
}
