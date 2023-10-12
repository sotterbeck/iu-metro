plugins {
    id("java-library")
}

group = "de.sotterbeck"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":usecase"))
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.flywaydb:flyway-core:9.21.0")
    testImplementation("org.postgresql:postgresql:42.6.0")
    testImplementation("org.testcontainers:postgresql:1.19.0")
    testImplementation("org.testcontainers:junit-jupiter:1.18.3")
    testImplementation("com.radcortez.flyway:flyway-junit5-extension:1.4.1")
    testImplementation("org.hibernate:hibernate-core:6.2.7.Final")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.assertj:assertj-db:2.0.2")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.test {
    useJUnitPlatform()
}