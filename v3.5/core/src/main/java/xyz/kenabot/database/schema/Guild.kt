/*
 * File:          Guild.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\database\schema\Guild.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: a13565304a5c4ec5080a846be1410b4a50bde4d26483a7cc45155fa2cbfd05ee
 *
 */


package xyz.kenabot.database.schema

import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import xyz.kenabot.database.Mongo

@Serializable
data class Guild(
    val guildID: String,
    val language: String,
    val embedColor: String,

    val radioChannelID: String,
    val radioEnabled: Boolean,

    val premiumExpires: String,
    val premiumEnabled: Boolean,

    val blockedReason: String,
    val blockedEnabled: Boolean,

    val disabledChannels: List<String>,
    val disabledCommands: List<String>,
    val disabledVoiceChannels: List<String>,

    val beta: Boolean,
    val silent: Boolean,
    val djRoleID: String,
    val autoplay: Boolean,

    val maxSongDuration: Long,
    val blockedSongs: List<String>,
    val explicitFilter: Boolean,

    val found: Boolean,
) {

    companion object {
        private const val tableName = "Guilds"
        private val collection = Mongo.main.getCollection<Guild>(tableName)

        val default = Guild(
            guildID = "",
            language = "es-ES",
            embedColor = "#2f3136",

            radioChannelID = "",
            radioEnabled = false,

            premiumExpires = "",
            premiumEnabled = false,

            blockedReason = "",
            blockedEnabled = false,

            disabledChannels = listOf(),
            disabledCommands = listOf(),
            disabledVoiceChannels = listOf(),

            beta = false,
            silent = false,
            djRoleID = "",
            autoplay = false,

            maxSongDuration = 0,
            blockedSongs = listOf(),
            explicitFilter = false,

            found = false,
        )

        suspend fun get(guildID: String): Guild? = collection.find(Filters.eq("guildID", guildID)).firstOrNull()
        suspend fun get(guildID: Long): Guild? = get(guildID.toString())

    }
}