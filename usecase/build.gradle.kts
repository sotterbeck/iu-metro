plugins {
    id("java-library")
}

group = "de.sotterbeck"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":entity"))
    compileOnly("org.jetbrains:annotations:24.0.1")
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
}

tasks.test {
    useJUnitPlatform()
}