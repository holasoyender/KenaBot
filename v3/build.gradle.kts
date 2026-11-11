import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
}

apply(plugin = "java")
apply(plugin = "idea")
apply(plugin = "application")
apply(plugin = "com.github.johnrengelman.shadow")

group = "kenabot"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://duncte123.jfrog.io/artifactory/maven")
    maven("https://jcenter.bintray.com/")
    maven("https://jitpack.io")
    maven("https://m2.dv8tion.net/releases")
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.12") {
        exclude(module = "opus-java")
    }
    implementation("ch.qos.logback:logback-classic:1.4.8")
    implementation("io.sentry:sentry:6.28.0")
    implementation("io.sentry:sentry-logback:6.28.0")
    //implementation("me.duncte123:botCommons:2.3.11")
    implementation("commons-io:commons-io:2.13.0")
    //implementation("club.minnced:discord-webhooks:0.8.2") posibles errores de memoria
    //implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.json:json:20230618")
    implementation("org.mongodb:mongodb-driver-sync:4.10.2")
    implementation("redis.clients:jedis:4.4.3")
    implementation("org.openjdk.nashorn:nashorn-core:15.4")
    implementation("com.github.walkyst:lavaplayer-fork:1.4.2")
    implementation("com.github.sedmelluq.jda-nas:jda-nas:master") {
        exclude(module="udp-queue")
    }

    implementation("org.java-websocket:Java-WebSocket:1.5.4")

    implementation("club.minnced:udpqueue-native-linux-x86-64:0.2.6")
    implementation("club.minnced:udpqueue-native-linux-aarch64:0.2.6")
    implementation("club.minnced:udpqueue-native-linux-arm:0.2.7")
    implementation("club.minnced:udpqueue-native-win-x86-64:0.2.7")

    implementation("com.sedmelluq:lavaplayer-ext-youtube-rotator:0.2.3") {
        this.exclude("com.sedmelluq", "lavaplayer")
    }

    api("se.michaelthelin.spotify:spotify-web-api-java:8.0.0")
    implementation("com.github.natanbc:lavadsp:0.7.7")

    implementation("org.jsoup:jsoup:1.16.1")

    implementation("io.prometheus:simpleclient:0.16.0")
    implementation("io.prometheus:simpleclient_hotspot:0.16.0")
    implementation("io.prometheus:simpleclient_httpserver:0.16.0")
    //implementation("io.prometheus:simpleclient_pushgateway:0.16.0")

    implementation("com.typesafe:config:1.4.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
}

configurations {
    compileOnly {
        extendsFrom(annotationProcessor.get())
    }
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "18" }
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:unchecked")
    options.compilerArgs.add("-Xlint:deprecation")
}

application { mainClass.set("Main") }

tasks {
    build {
        doLast {
            println("Version: $version")
        }
    }
}
