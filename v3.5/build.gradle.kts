import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.dokka") version "1.8.20" apply false
    id("org.ajoberstar.grgit") version "5.2.0"
    id("org.springframework.boot") version "3.1.0" apply false
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0" apply false
}

allprojects {
    group = "xyz.kenabot"
    version = "4.0.1-dev"
    description = "El bot de música que necesitas para tu servidor de Discord!"

    repositories {
        mavenCentral()
        maven("https://duncte123.jfrog.io/artifactory/maven")
        maven("https://m2.dv8tion.net/releases")
        maven("https://jitpack.io")
        maven("https://jcenter.bintray.com/")
        maven("https://maven.arbjerg.dev/releases")
        google()
    }

}

buildscript {

    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        maven("https://repo.spring.io/plugins-release")
        maven("https://jitpack.io")
        maven("https://m2.dv8tion.net/releases")
    }
    dependencies {
        classpath("com.github.johnrengelman:shadow:8.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("org.springframework.boot:spring-boot-gradle-plugin:3.1.2")
    }

}

subprojects {

    apply(plugin = "java")
    apply(plugin = "kotlin")

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "18"
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
    }

}