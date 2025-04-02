import org.jooq.meta.jaxb.Property

plugins {
    id("java-library")
    id("nu.studer.jooq") version "9.0"
}

group = "de.sotterbeck"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":usecase"))
    implementation(libs.jooq)
    jooqGenerator(libs.slf4j.simple)
    jooqGenerator("com.github.sabomichal:jooq-meta-postgres-flyway:1.2.0")

    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.postgresql)
    testImplementation(libs.flywaydb)
    testImplementation(libs.flywaydb.database.postgresql)
    testImplementation(libs.assertj.core)
    testImplementation(libs.assertj.db)
    testImplementation(libs.junit.jupiter)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
}

jooq {
    configurations {
        create("main") {
            jooqConfiguration.apply {
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "com.github.sabomichal.jooq.PostgresDDLDatabase"
                        inputSchema = "public"
                        includes = "public.*"
                        excludes = "flyway_schema_history"
                        properties = listOf(
                            Property().withKey("locations").withValue("src/main/resources/db/migration"),
                            Property().withKey("dockerImage").withValue("postgres:15")
                        )
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                        isDaos = true
                    }
                    target.apply {
                        packageName = "de.sotterbeck.iumetro.dataprovider.postgres.jooq.generated"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}
