/*
 * File:          Regions.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\database\schema\Regions.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: aaad9bcf9595345740a559e4602ccba806d475dc79ecfd02b51c6a4b1bc1b041
 *
 */


package xyz.kenabot.database.schema

class Regions(
    region: String,
    count: Int
) {
    companion object {
        const val tableName = "Regions"
    }
}