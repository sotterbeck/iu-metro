plugins {
    id("java")
    id("com.github.johnrengelman.shadow").version("8.1.1")
    id("xyz.jpenilla.run-paper").version("2.3.0")
}

group = "de.sotterbeck"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":usecase"))
    implementation(project(":dataproviders:postgres"))
    implementation(libs.guice)
    implementation(libs.flywaydb)
    implementation(libs.flywaydb.database.postgresql)
    implementation(libs.hikaricp)
    implementation(libs.postgresql)
    implementation(libs.jooq)
    implementation(libs.cloud.core)
    implementation(libs.cloud.annotations)
    implementation(libs.cloud.paper)
    compileOnly(libs.paper)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit)
    testImplementation(libs.mockbukkit)
    testImplementation(libs.paper)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
}

tasks {
    test {
        useJUnitPlatform()
    }

    runServer {
        minecraftVersion("1.20.4")
    }

    shadowJar {
        mergeServiceFiles()
    }
}
