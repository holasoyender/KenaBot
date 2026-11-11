plugins {
    java
}

group = "xyz.kenabot"
version = "4.0.1-dev"
description = "Shared modules for the KenaBot project"

repositories {
    mavenCentral()
}

dependencies {

    implementation(libs.jda)
    implementation(libs.json)

}