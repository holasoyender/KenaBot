/*
 * File:          Env.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\config\Env.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: 39fa69c6b16ab9dd933f98df36dc4cbc0276ef1a0316a4f1c5013ea18dcf4196
 *
 */


package xyz.kenabot.config

import dev.minn.jda.ktx.util.SLF4J
import xyz.kenabot.config.env.Dotenv
import kotlin.system.exitProcess

object Env {

    private val logger by SLF4J

    /**
     * The bot's token, used to log in to Discord.
     */
    lateinit var TOKEN: String

    /**
     * The database connection URL, used to connect to the MongoDB database.
     */
    lateinit var DATABASE_URL: String

    /**
     * The Redis connection URL, used to connect to the Redis server.
     */
    lateinit var REDIS_URL: String

    /**
     * The Sentry DSN, used to connect to Sentry.
     */
    lateinit var SENTRY_DSN: String

    /**
     * The Sentry environment.
     * This is used to differentiate between different environments, such as production, staging, etc.
     */
    var SENTRY_ENVIRONMENT: String = "production"

    /**
     * The genius API key, used to connect to the genius API.
     */
    lateinit var GENIUS_API: String

    /**
     * The last.fm API key, used to connect to the last.fm API.
     */
    lateinit var LASTFM_API_KEY: String

    /**
     * The webhook URL, used to send webhooks.
     */
    lateinit var WEBHOOK_URL: String

    /**
     * The master URL, used to connect to the master server.
     */
    lateinit var MASTER_API_URL: String

    init {
        load()
    }

    /**
     * Loads the environment variables from the .env file.
     * @return void
     */
    fun load(path: String = ".env") {

        val dotenv: Dotenv
        try {
            dotenv = Dotenv.configure().filename(path).ignoreIfMissing().ignoreIfMalformed().load()
        } catch (e: Exception) {
            logger.error("Failed to load .env file", e)
            exitProcess(1)
        }

        TOKEN = getEnv("TOKEN", dotenv, true)

        DATABASE_URL = getEnv("DATABASE_URL", dotenv, true)
        REDIS_URL = getEnv("REDIS_URL", dotenv, true)

        SENTRY_DSN = getEnv("SENTRY_DSN", dotenv, true)
        SENTRY_ENVIRONMENT = getEnv("SENTRY_ENVIRONMENT", dotenv)

        GENIUS_API = getEnv("GENIUS_API", dotenv, true)
        LASTFM_API_KEY = getEnv("LASTFM_API_KEY", dotenv, true)

        WEBHOOK_URL = getEnv("WEBHOOK_URL", dotenv, true)
        MASTER_API_URL = getEnv("MASTER_API_URL", dotenv, true)

        try {
            checkNull()
            logger.info("Env variables loaded")
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            exitProcess(1)
        }

    }

    /**
     * Gets an environment variable from the system or from the .env file.
     * @param key The key of the environment variable.
     * @param dotenv The dotenv instance.
     * @throws Exception If the environment variable is not found.
     * @return String
     */
    private fun getEnv(key: String, dotenv: Dotenv, required: Boolean = false): String {
        if (System.getenv(key) != null) {
            return System.getenv(key)
        } else if (dotenv.get(key) != null) {
            return dotenv.get(key)
        }

        if (!required)
            return ""

        throw Exception("Missing environment variable: '$key', please make sure that the .env file is present and that all required variables are set.")
    }

    private fun checkNull() {
        for (f in Env::class.java.declaredFields) {
            val exist = f[Env::class.java] != null
            if (!exist) {
                throw IllegalAccessException("Variable " + f.name + " is null")
            }
        }
    }
}