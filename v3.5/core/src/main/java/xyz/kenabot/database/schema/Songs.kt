/*
 * File:          Songs.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\database\schema\Songs.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: 94a01e808d6bae933af327cde51465068e5e629ae92505f080154273fd2a174d
 *
 */


package xyz.kenabot.database.schema

import kotlinx.serialization.Serializable

@Serializable
data class Songs(
    val url: String,
    val plays: Int
) {
    companion object {
        const val tableName = "Songs"
    }
}