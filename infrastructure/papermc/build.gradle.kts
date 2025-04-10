plugins {
    id("java")
    id("com.gradleup.shadow").version("8.3.0")
    id("xyz.jpenilla.run-paper").version("2.3.0")
}

group = "de.sotterbeck"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":application"))
    implementation(project(":infrastructure:postgres"))
    implementation(libs.guice)
    implementation(libs.flywaydb)
    implementation(libs.flywaydb.database.postgresql)
    implementation(libs.hikaricp)
    implementation(libs.javalin)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.datatype.jdk8)
    implementation(libs.slf4j.simple)
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
        val relocatedPackage = "de.sotterbeck.iumetro.libs"

        val relocations = mapOf<String, String>(
            "com.google" to "$relocatedPackage.google",
            "com.fasterxml" to "$relocatedPackage.fasterxml",
            "com.zaxxer" to "$relocatedPackage.zaxxer",
            "io.leangen.geantyref" to "$relocatedPackage.geantyref",
            "io.r2dbc" to "$relocatedPackage.r2dbc",
            "jakarta.inject" to "$relocatedPackage.jakarta.inject",
            "jakarta.servlet" to "$relocatedPackage.jakarta.servlet",
            "javax.inject" to "$relocatedPackage.javax.inject",
            "javax.servlet" to "$relocatedPackage.javax.servlet",
            "javax.annotation" to "$relocatedPackage.javax.annotation",
            "org.aopalliance" to "$relocatedPackage.aopalliance",
            "org.checkerframework" to "$relocatedPackage.checkerframework",
            "org.flywaydb" to "$relocatedPackage.flywaydb",
            "org.incendo" to "$relocatedPackage.incendo",
            "org.jooq" to "$relocatedPackage.jooq",
            "org.postgresql" to "$relocatedPackage.postgresql",
            "org.reactivestreams" to "$relocatedPackage.reactiveStreams",
            "org.slf4j" to "$relocatedPackage.slf4j",
            "io.javalin" to "$relocatedPackage.javalin",
            "kotlin" to "$relocatedPackage.kotlin",
            "org.eclipse.jetty" to "$relocatedPackage.eclipse.jetty",
            "org.intellij" to "$relocatedPackage.intellij",
            "org.jetbrains.annotations" to "$relocatedPackage.jetbrains.annotations",

        )

        relocations.forEach { (original, relocated) ->
            relocate(original, relocated)
        }

        mergeServiceFiles()
    }
}
