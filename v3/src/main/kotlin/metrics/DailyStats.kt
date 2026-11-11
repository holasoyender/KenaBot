/*
 * File:          DailyStats.kt
 * Path:          v3\src\main\kotlin\metrics\DailyStats.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-16 23:09:28 +01:00
 *
 * SHA-256: 4112af02556f5e946741d3be88206f5f203a29f7af040b0ef5f80479f42864a0
 *
 */


package metrics

import io.prometheus.client.Collector
import io.prometheus.client.Counter
import net.dv8tion.jda.api.JDA

object DailyStats {

    private val TODAY_NEW_GUILDS = Counter.build()
        .name("kenabot_today_guilds")
        .help("Today guilds of the bot")
        .labelNames("bot_name")
        .create()

    private val TODAY_REMOVED_GUILDS = Counter.build()
        .name("kenabot_today_removed_guilds")
        .help("Today guilds of the bot")
        .labelNames("bot_name")
        .create()

    private val TODAY_COMMANDS = Counter.build()
        .name("kenabot_today_commands")
        .help("Today commands of the bot")
        .labelNames("bot_name")
        .create()

    private val TODAY_SONGS = Counter.build()
        .name("kenabot_today_songs")
        .help("Today songs of the bot")
        .labelNames("bot_name")
        .create()

    fun register(name: String) {
        TODAY_NEW_GUILDS.register<Collector>()
        TODAY_REMOVED_GUILDS.register<Collector>()
        TODAY_COMMANDS.register<Collector>()
        TODAY_SONGS.register<Collector>()

        TODAY_NEW_GUILDS.labels(name).inc()
        TODAY_REMOVED_GUILDS.labels(name).inc()
        TODAY_COMMANDS.labels(name).inc()
        TODAY_SONGS.labels(name).inc()
    }

    fun clear() {
        TODAY_NEW_GUILDS.clear()
        TODAY_REMOVED_GUILDS.clear()
        TODAY_COMMANDS.clear()
        TODAY_SONGS.clear()
    }

    fun incrementGuilds(jda: JDA) {
        TODAY_NEW_GUILDS.labels(jda.selfUser.name).inc()
    }
    fun decrementGuilds(jda: JDA) {
        TODAY_REMOVED_GUILDS.labels(jda.selfUser.name).inc()
    }
    fun incrementCommands(jda: JDA) {
        TODAY_COMMANDS.labels(jda.selfUser.name).inc()
    }
    fun incrementSongs(jda: JDA) {
        TODAY_SONGS.labels(jda.selfUser.name).inc()
    }
}