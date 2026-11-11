plugins {
    java
    application
}

group = "xyz.kenabot.proxy"
version = "1.0"
description = "This is a simple HTTP proxy that allows KenaBot to connect to other services."

application {
    mainClass.set("xyz.kenabot.proxy.Proxy")
}

tasks {
    build {
        doLast {
            println("Proxy built successfully!")
        }
    }
}