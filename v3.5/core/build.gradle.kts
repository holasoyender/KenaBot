import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    kotlin("jvm") version "1.9.0"
    application
    java
    kotlin("plugin.serialization") version "1.9.0"
    id("io.sentry.jvm.gradle") version "3.13.0"
}

group = "xyz.kenabot.core"
version = "4.0.1"
description = "Core application for KenaBot"

application {
    mainClass.set("xyz.kenabot.Main")
}

configurations {
    compileOnly {
        extendsFrom(annotationProcessor.get())
    }
}

dependencies {

    implementation(projects.shared)

    implementation(libs.jda)
    implementation(libs.ktx)

    implementation(libs.classgraph)
    implementation(libs.sentry.logback)

    implementation(libs.mongodb)
    implementation(libs.mongodb.serialization)
    implementation(libs.logback)

    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
}

tasks {
    build {
        doLast {
            println("Version: $version")
        }
    }

    withType<BootJar> {
        archiveFileName = "KenaBot.jar"
    }

    withType<BootRun> {
        dependsOn("compileTestJava")

        //pass in custom jvm args
        // source: https://stackoverflow.com/a/25079415
        // example: ./gradlew bootRun -PjvmArgs="--illegal-access=debug -Dwhatever=value"
        if (project.hasProperty("jvmArgs")) {
            val args = project.property("jvmArgs")
                .toString()
                .split("\\s".toPattern())

            jvmArgs?.addAll(args)
        }
    }
}