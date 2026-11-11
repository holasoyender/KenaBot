/*
 * File:          Server.kt
 * Path:          v3\src\main\kotlin\metrics\Server.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-14 18:13:13 +02:00
 *
 * SHA-256: f7b2b073e0c88d85be43f8f8bb51b66afdb719e6ce47182508a01f3630a3346b
 *
 */


package metrics

import io.prometheus.client.exporter.HTTPServer
import io.prometheus.client.hotspot.DefaultExports
import net.dv8tion.jda.api.sharding.ShardManager
import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory
import javax.management.NotificationEmitter
import kotlin.concurrent.thread

fun start(shardManager: ShardManager) {

    val logger = LoggerFactory.getLogger("Prometheus")

    var port = 5055
    when (shardManager.getShardById(0)!!.selfUser.name) {
        "KenaBot" -> port = 5051
        "KenaBot 2" -> port = 5052
        "KenaBot 3" -> port = 5053
        "KenaBot 4" -> port = 5054
    }

    HTTPServer.Builder()
        .withPort(port)
        .build()

    DefaultExports.initialize()
    DiscordLatency.register()
    JDAStats.register()
    DatabaseStats.register()
    DailyStats.register(shardManager.getShardById(0)!!.selfUser.name)
    PlayerStats.register()
    PlayerGC.register()
    Regions.register()

    thread(isDaemon = true, name = "MetricsServer") {

        while (true) {
            try {
                JDAStats.update(shardManager)
                DiscordLatency.update(shardManager)
                DatabaseStats.update()
                Regions.update()
                PlayerStats.update(shardManager.getShardById(0)!!.selfUser.name)

                val currentHour = java.time.LocalDateTime.now().hour
                if (currentHour == 0) {
                    val minutes = java.time.LocalDateTime.now().minute
                    if (minutes in 0..5) {
                        DailyStats.clear()
                    }
                }

            } catch (e: Exception) {
                logger.error("Error updating metrics: ${e.message}")
            }

            Thread.sleep(15000)
        }
    }

    val gcNotificationListener = GcNotificationListener()
    for (gcBean in ManagementFactory.getGarbageCollectorMXBeans()) {
        if (gcBean is NotificationEmitter) {
            (gcBean as NotificationEmitter).addNotificationListener(gcNotificationListener, null, gcBean)
        }
    }

    logger.info("Started metrics server on port $port")
}