/*
 * File:          PlayerStats.kt
 * Path:          v3\src\main\kotlin\metrics\PlayerStats.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: 49abb469709188542f682ca6723837f491ef255f274d4e949bf9e923a68468dd
 *
 */


package metrics

import io.prometheus.client.Collector
import io.prometheus.client.Gauge
import player.Player

object PlayerStats {

    private val PLAYER_COUNT = Gauge.build()
        .name("kenabot_player_count")
        .help("Total players of the bot")
        .labelNames("bot_name")
        .create()

    private val PLAYING_PLAYERS_COUNT = Gauge.build()
        .name("kenabot_playing_players_count")
        .help("Total playing players of the bot")
        .labelNames("bot_name")
        .create()

    fun register() {
        PLAYER_COUNT.register<Collector>()
        PLAYING_PLAYERS_COUNT.register<Collector>()
    }

    fun update(name: String) {

        val totalPlayers = Player.musicManagers.toMap()
        val playingPlayers = totalPlayers.filter { it.value.player.playingTrack != null }

        PLAYER_COUNT.labels(name).set(totalPlayers.size.toDouble())
        PLAYING_PLAYERS_COUNT.labels(name).set(playingPlayers.size.toDouble())
    }

}