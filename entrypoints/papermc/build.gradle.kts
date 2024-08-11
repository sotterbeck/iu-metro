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
        val relocatedPackage = "de.sotterbeck.iumetro.libs"

        val relocations = mapOf<String, String>(
            "com.google" to "$relocatedPackage.google",
            "com.fasterxml" to "$relocatedPackage.fasterxml",
            "com.zaxxer" to "$relocatedPackage.zaxxer",
            "io.leangen.geantyref" to "$relocatedPackage.geantyref",
            "io.r2dbc" to "$relocatedPackage.r2dbc",
            "jakarta.inject" to "$relocatedPackage.jakarta.inject",
            "javax.inject" to "$relocatedPackage.javax.inject",
            "javax.annotation" to "$relocatedPackage.javax.annotation",
            "org.aopalliance" to "$relocatedPackage.aopalliance",
            "org.checkerframework" to "$relocatedPackage.checkerframework",
            "org.flywaydb" to "$relocatedPackage.flywaydb",
            "org.incendo" to "$relocatedPackage.incendo",
            "org.jooq" to "$relocatedPackage.jooq",
            "org.postgresql" to "$relocatedPackage.postgresql",
            "org.reactivestreams" to "$relocatedPackage.reactiveStreams",
            "org.slf4j" to "$relocatedPackage.slf4j",
        )

        relocations.forEach { (original, relocated) ->
            relocate(original, relocated)
        }

        mergeServiceFiles()
    }
}
