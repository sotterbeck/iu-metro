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
    implementation(libs.hikaricp)
    implementation(libs.postgresql)
    implementation("dev.jorel:commandapi-bukkit-shade:9.1.0")
    implementation(libs.jooq)
    compileOnly(libs.paper)
    testImplementation(libs.junit.jupiter)
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
}
