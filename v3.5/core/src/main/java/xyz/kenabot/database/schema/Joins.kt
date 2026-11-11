/*
 * File:          Joins.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\database\schema\Joins.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: 71b34e28f244133d61d17f28bb517a73e5df3690458f44d32f4b78bf8cc6c823
 *
 */


package xyz.kenabot.database.schema

data class Joins(
    val channelID: String,
    val joins: Int
) {
    companion object {
        const val tableName = "Joins"
    }
}