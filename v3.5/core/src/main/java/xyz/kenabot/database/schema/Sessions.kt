/*
 * File:          Sessions.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\database\schema\Sessions.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: 38fda3d5d3ea4ad58b009048fd518e0922329f585df84a6d39f7be3c76ad2ede
 *
 */


package xyz.kenabot.database.schema

import kotlinx.serialization.Serializable

@Serializable
data class Sessions(
    val sourceGuildId: String,
    val sourceBotName: String,
    val inviteCode: String,
    val createdAt: Long,
    val listeners: List<TogetherListener>
)

@Serializable
data class TogetherListener(
    val guildId: String,
    val botName: String,
    val channelId: String
)