/*
 * File:          Main.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\Main.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 22f23681d19dedb2c5cb06a406150ec7b4751a44ccf6f1d1c8333500764ab399
 *
 */


package xyz.kenabot

import dev.minn.jda.ktx.util.SLF4J
import net.dv8tion.jda.api.sharding.ShardManager
import okhttp3.OkHttpClient
import xyz.kenabot.config.Env
import xyz.kenabot.database.Mongo
import xyz.kenabot.database.Redis
import xyz.kenabot.locale.LanguageLoader
import xyz.kenabot.managers.CommandRegistry
import xyz.kenabot.managers.SentryManager
import java.lang.management.ManagementFactory
import java.util.logging.Logger
import kotlin.system.exitProcess

object Main {

    private val logger by SLF4J
    private lateinit var shardingManager: ShardManager

    @JvmStatic
    fun main(args: Array<String>) {

        val isDebug = ManagementFactory.getRuntimeMXBean().inputArguments.toString().indexOf("jdwp") >= 0
        if (isDebug) {
            logger.warn("Debugger is attached, running in debug mode, some features may not work as expected.")
            Globals.isDebug = true
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")
        }

        val jvmVersion = System.getProperty("java.version")
        if (jvmVersion.split(".").first().toInt() < 18) {
            logger.error("The JVM version is lower than 18, please update your JVM to run this application.")
            exitProcess(1)
        }

        Env.load(args.firstOrNull() ?: ".env")

        SentryManager.loadSentry(Env.SENTRY_DSN, Env.SENTRY_ENVIRONMENT)

        logger.info("Command registry started, registry size is ${CommandRegistry().total()} command(s).")

        val (total, ok) = LanguageLoader.load()
        logger.info("Language manager loaded $ok/$total language(s).")

        shardingManager = Discord.loadShardingManager()

        Mongo.connect(shardingManager)
        Redis.connect(shardingManager)

        Logger.getLogger(OkHttpClient::class.java.name).level = java.util.logging.Level.FINE

    }
}