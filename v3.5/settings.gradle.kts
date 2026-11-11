rootProject.name = "Kena"

include("core")
include("proxy")
include("atenea")
include("shared")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            database()
            spring()
            voice()
            metrics()
            common()
            discord()
        }
    }
}

fun VersionCatalogBuilder.database() {
    version("mongodb", "4.10.1")

    library("mongodb", "org.mongodb", "mongodb-driver-kotlin-coroutine").versionRef("mongodb")
    library("mongodb-serialization", "org.mongodb", "bson-kotlinx").versionRef("mongodb")

    library("redis", "redis.clients", "jedis").version("")
}

fun VersionCatalogBuilder.spring() {
    version("spring-boot", "3.1.0")

    library("spring-websocket", "org.springframework", "spring-websocket").version("6.0.9")

    library("spring-boot",          "org.springframework.boot", "spring-boot").versionRef("spring-boot")
    library("spring-boot-web",      "org.springframework.boot", "spring-boot-starter-web").versionRef("spring-boot")
    library("spring-boot-undertow", "org.springframework.boot", "spring-boot-starter-undertow") .versionRef("spring-boot")
    library("spring-boot-test",     "org.springframework.boot", "spring-boot-starter-test") .versionRef("spring-boot")

    bundle("spring", listOf("spring-websocket", "spring-boot-web", "spring-boot-undertow"))
}

fun VersionCatalogBuilder.voice() {
    version("lavaplayer", "2.0.0")

    library("lavaplayer",            "dev.arbjerg", "lavaplayer").versionRef("lavaplayer")
    library("lavaplayer-ip-rotator", "dev.arbjerg", "lavaplayer-ext-youtube-rotator").versionRef("lavaplayer")
    library("lavadsp",               "dev.arbjerg", "lavadsp").version("0.7.8")

    library("koe",          "moe.kyokobot.koe", "core").version("2.0.0-rc1")
    library("koe-udpqueue", "moe.kyokobot.koe", "ext-udpqueue").version("2.0.0-rc1")

    version("udpqueue", "0.2.7")
    val platforms = listOf("linux-x86-64", "linux-x86", "linux-aarch64", "linux-arm", "linux-musl-x86-64", "linux-musl-aarch64", "win-x86-64", "win-x86", "darwin")
    platforms.forEach {
        library("udpqueue-native-$it", "club.minnced", "udpqueue-native-$it").versionRef("udpqueue")
    }

    bundle("udpqueue-natives", platforms.map { "udpqueue-native-$it" })
}

fun VersionCatalogBuilder.metrics() {
    version("prometheus", "0.16.0")

    library("metrics",         "io.prometheus", "simpleclient").versionRef("prometheus")
    library("metrics-hotspot", "io.prometheus", "simpleclient_hotspot").versionRef("prometheus")
    library("metrics-logback", "io.prometheus", "simpleclient_logback").versionRef("prometheus")
    library("metrics-servlet", "io.prometheus", "simpleclient_servlet").versionRef("prometheus")

    bundle("metrics", listOf("metrics", "metrics-hotspot", "metrics-logback", "metrics-servlet"))
}

fun VersionCatalogBuilder.discord() {

    library("jda", "net.dv8tion", "JDA").version("5.0.0-beta.15")
    library("ktx", "com.github.minndevelopment", "jda-ktx").version("0.10.0-beta.1")

    bundle("discord", listOf("jda", "ktx"))
}

fun VersionCatalogBuilder.common() {
    version("kotlin", "1.9.0")

    library("kotlin-reflect",     "org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin")
    library("kotlin-stdlib-jdk8", "org.jetbrains.kotlin", "kotlin-stdlib-jdk8").versionRef("kotlin")

    library("kotlinx-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.5.1")
    library("kotlinx-datetime", "org.jetbrains.kotlinx", "kotlinx-datetime").version("0.4.0")
    library("kotlinx-serialization", "org.jetbrains.kotlinx", "kotlinx-serialization-core").version("1.5.1")

    library("logback",        "ch.qos.logback",       "logback-classic").version("1.4.7")
    library("sentry-logback", "io.sentry",            "sentry-logback").version("6.22.0")
    library("oshi",           "com.github.oshi",      "oshi-core").version("6.4.3")
    library("commons", "commons-io", "commons-io").version("2.6")

    library("classgraph", "io.github.classgraph", "classgraph").version("4.8.161")
    library("json", "org.json", "json").version("20230618")
    library("annotations", "org.jetbrains", "annotations").version("24.0.1")
}