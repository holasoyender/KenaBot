/*
 * File:          Commands.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\database\schema\Commands.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: cd67c1d0668139403223240dbc3050e6b4b930f1d739fb3298e64513b1dcba10
 *
 */


package xyz.kenabot.database.schema

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class Commands(
    @SerialName("_id")
    @Contextual val id: ObjectId?,
    val command: String,
    val uses: Int
) {

    companion object {
        const val tableName = "Commands"
    }

}