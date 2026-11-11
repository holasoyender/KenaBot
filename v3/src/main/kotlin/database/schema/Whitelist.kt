/*
 * File:          Whitelist.kt
 * Path:          v3\src\main\kotlin\database\schema\Whitelist.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: e109bc10d817af597ffb6b8b50ce8a341418577922861e1a46cb99bf5f69b2c2
 *
 */


package database.schema

import database.Database
import org.bson.Document

object Whitelist {

    private var status = false

    fun addServer(guildID: String) {
        val database = Database.statsDatabase!!

        val whitelistCollection = database.getCollection("Whitelist")
        val whitelistDB = whitelistCollection.find(Document("id", guildID))
        if (whitelistDB.first() == null) {
            whitelistCollection.insertOne(Document("id", guildID).append("enabled", true))
        }
    }

    fun removeServer(guildID: String) {
        val database = Database.statsDatabase!!

        val whitelistCollection = database.getCollection("Whitelist")
        if (whitelistCollection.find(Document("id", guildID)).first() != null) {
            whitelistCollection.deleteOne(Document("id", guildID))
        }
    }

    fun isWhitelisted(guildID: String): Boolean {
        val database = Database.statsDatabase!!
        val whitelistCollection = database.getCollection("Whitelist")
        val whitelistDB = whitelistCollection.find(Document("id", guildID))
        return whitelistDB.first() != null
    }

    fun enableWhitelist() {
        status = true
    }
    fun disableWhitelist() {
        status = false
    }

    fun isWhitelistEnabled(): Boolean {
        return status
    }


}