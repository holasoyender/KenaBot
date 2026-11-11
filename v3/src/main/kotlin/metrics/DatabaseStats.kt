/*
 * File:          DatabaseStats.kt
 * Path:          v3\src\main\kotlin\metrics\DatabaseStats.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 449057be2d75d978959bf1a60d30a5204a17ca862430aeb9e617fd3f501835c9
 *
 */


package metrics

import Main
import database.schema.Commands
import database.schema.Guild
import database.schema.Joins
import database.schema.Songs
import io.prometheus.client.Collector
import io.prometheus.client.Gauge

object DatabaseStats {

    private val TOTAL_GUILD_CONFIGS = Gauge.build()
        .name("kenabot_guild_configs")
        .help("Total configurations of the bot")
        .labelNames("bot_name")
        .create()

    private val TOTAL_SONGS = Gauge.build()
        .name("kenabot_songs")
        .help("Total songs played by the bot")
        .create()

    private val TOTAL_JOINS = Gauge.build()
        .name("kenabot_voice_joins")
        .help("Total joins of the bot")
        .labelNames("channelID")
        .create()

    private val TOTAL_COMMANDS = Gauge.build()
        .name("kenabot_commands_executed")
        .help("Total commands executed")
        .create()

    fun register() {
        TOTAL_GUILD_CONFIGS.register<Collector>()
        TOTAL_SONGS.register<Collector>()
        TOTAL_JOINS.register<Collector>()
        TOTAL_COMMANDS.register<Collector>()
    }

    fun update() {
        TOTAL_GUILD_CONFIGS.labels(Main.shards!!.getShardById(0)?.selfUser?.name ?: "KenaBot Alpha").set(Guild.getCollection().countDocuments().toDouble())
        TOTAL_SONGS.set(Songs.countDocuments().toDouble())

        val top10Joins = Joins.getTop10()

        top10Joins.forEach {
            TOTAL_JOINS.labels(it.channelID).set((it.joins).toDouble())
        }

        TOTAL_COMMANDS.set(Commands.getAll().sumOf { it.uses.toDouble() })
    }

}