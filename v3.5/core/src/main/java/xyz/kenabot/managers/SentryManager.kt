/*
 * File:          SentryManager.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\managers\SentryManager.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 8dc9301d0285cfcc52347dafff7c9fb4133c6ff9ce78d772f844d4770c03739f
 *
 */


package xyz.kenabot.managers

import dev.minn.jda.ktx.util.SLF4J
import io.sentry.logback.SentryAppender
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.classic.Level
import io.sentry.Sentry
import org.slf4j.Logger

object SentryManager {

    private val logger by SLF4J
    private const val SENTRY_APPENDER_NAME = "SENTRY"
    @get:Synchronized
    private val sentryLogbackAppender: SentryAppender
        // programmatically creates a sentry appender
        get() {
            val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
            val root = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME)

            var sentryAppender = root.getAppender(SENTRY_APPENDER_NAME) as? SentryAppender
            if (sentryAppender == null) {

                sentryAppender = SentryAppender()
                sentryAppender.name = SENTRY_APPENDER_NAME

                val warningsOrAboveFilter = ThresholdFilter()

                warningsOrAboveFilter.setLevel(Level.WARN.levelStr)
                warningsOrAboveFilter.start()

                sentryAppender.addFilter(warningsOrAboveFilter)
                sentryAppender.context = loggerContext

                root.addAppender(sentryAppender)
            }
            return sentryAppender
        }

    fun loadSentry(dsn: String, env: String) {

        logger.info("Turning on sentry...")
        Sentry.init {
            it.dsn = dsn
            it.environment = env
            it.tracesSampleRate = 1.0
            it.isDebug = false
            it.isAttachStacktrace = true
        }
        sentryLogbackAppender.start()

    }

    @Suppress("unused")
    fun off() {
        logger.warn("Turning off sentry...")
        Sentry.close()
        sentryLogbackAppender.stop()
    }
}