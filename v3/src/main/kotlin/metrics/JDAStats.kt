/*
 * File:          JDAStats.kt
 * Path:          v3\src\main\kotlin\metrics\JDAStats.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-30 15:25:05 +02:00
 *
 * SHA-256: 446cc48327acbce55f1fe623a133b3a8467d385620b4b7be5440e156ea096704
 *
 */


package metrics

import io.prometheus.client.Collector
import io.prometheus.client.Gauge
import net.dv8tion.jda.api.sharding.ShardManager

object JDAStats {

    private val TOTAL_GUILDS = Gauge.build()
        .name("kenabot_guilds")
        .help("Total guilds of the bot")
        .labelNames("bot_name")
        .create()

    private val TOTAL_USERS = Gauge.build()
        .name("kenabot_users")
        .help("Total users of the bot")
        .labelNames("bot_name")
        .create()

    fun register() {
        TOTAL_GUILDS.register<Collector>()
        TOTAL_USERS.register<Collector>()
    }

    fun update(shardManager: ShardManager) {

        val botGuilds = shardManager.guilds
        TOTAL_GUILDS.labels(shardManager.getShardById(0)!!.selfUser.name).set(botGuilds.size.toDouble())
        TOTAL_USERS.labels(shardManager.getShardById(0)!!.selfUser.name).set(botGuilds.sumOf { it.memberCount }.toDouble())
    }
}