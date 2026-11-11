/*
 * File:          Premium.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\database\schema\Premium.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: 2e8e11a2bea18300ab6f9f0b6934da031bec1df4a737ce19cf3b853074acdbd8
 *
 */


package xyz.kenabot.database.schema

data class Premium(
    val code: String,
    val duration: String,
    val createdAt: Long,
    val claimed: Boolean,
    val claimedAt: String,
    val claimedOn: Long?,
    val createdBy: String,
    val bots: List<String>
) {
    companion object {
        const val tableName = "Premiums"
    }
}