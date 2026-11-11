/*
 * File:          Env.kt
 * Path:          v3\src\main\kotlin\config\Env.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 5741cbabff762b2d21223594b3cb80a3e929a80154e79b333d801a5f0db1c786
 *
 */


package config

import Main.cmdArgs
import io.sentry.Sentry
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

object Env {
    var TOKEN: String? = null
    var SENTRY_DNS: String? = null
    var MONGODB_URL: String? = null
    var REDIS_HOST: String? = null
    //var SPOTIFY_ID: String? = null
    //var SPOTIFY_SECRET: String? = null
    //var SPOTIFY_SEARCH_ID: String? = null
    //var SPOTIFY_SEARCH_SECRET: String? = null
    var GENIUS_API: String? = null
    var WEBHOOK_URL: String? = null
    var PROXY: String? = null
    var CENTRAL_API_URL: String? = null
    var LASTFM_API_KEY: String? = null

    private val logger = LoggerFactory.getLogger("Env")

    init {
        val dotenv: Dotenv?
        try {

            var path = ".env"
            if(cmdArgs.isNotEmpty())
                path = cmdArgs[0]

            dotenv = Dotenv.configure().filename(path).ignoreIfMalformed().ignoreIfMissing().load()
        } catch (e: Exception) {
            logger.error("Error al cargar el archivo de configuración .env")
            exitProcess(1)
        }
        if (dotenv == null) {
            logger.error("Error al cargar el archivo de configuración .env")
            exitProcess(1)
        }

        TOKEN = getenv("BOT_TOKEN", dotenv)
        SENTRY_DNS = getenv("SENTRY_DNS", dotenv)
        MONGODB_URL = getenv("MONGODB_URL", dotenv)
        REDIS_HOST = getenv("REDIS_HOST", dotenv)
//        SPOTIFY_ID = getenv("SPOTIFY_ID", dotenv)
//        SPOTIFY_SECRET = getenv("SPOTIFY_SECRET", dotenv)
//        SPOTIFY_SEARCH_ID = getenv("SPOTIFY_SEARCH_ID", dotenv)
//        SPOTIFY_SEARCH_SECRET = getenv("SPOTIFY_SEARCH_SECRET", dotenv)
        GENIUS_API = getenv("GENIUS_API", dotenv)
        WEBHOOK_URL = getenv("WEBHOOK_URL", dotenv)
        PROXY = getenv("PROXY", dotenv)
        CENTRAL_API_URL = getenv("CENTRAL_API_URL", dotenv)
        LASTFM_API_KEY = getenv("LASTFM_API_KEY", dotenv)

        try {
            checkNull()
            logger.info("Env variables loaded")
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            Sentry.captureException(e)
            exitProcess(1)
        }
    }

    private fun getenv(name: String, dotenv: Dotenv?): String? {
        if (dotenv == null) {
            throw IllegalAccessException("Error al cargar el archivo de configuración .env")
        }

        if (System.getenv(name) != null) {
            return System.getenv(name)
        } else if (dotenv.get(name) != null) {
            return dotenv.get(name)
        }
        return null
    }

    private fun checkNull() {
        for (f in Env::class.java.declaredFields) {
            val exist = f[Env::class.java] != null
            if (!exist) {
                throw IllegalAccessException("La variable " + f.name + " es obligatoria")
            }
        }
    }
}