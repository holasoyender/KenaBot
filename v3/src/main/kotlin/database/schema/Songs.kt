/*
 * File:          Songs.kt
 * Path:          v3\src\main\kotlin\database\schema\Songs.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 16001ea6e15988c89b8394f2e87967f1668bb57b1e7bfc4c96944539f4d0893d
 *
 */


package database.schema

import database.Database
import org.bson.Document

class Songs(
    url: String,
    plays: Int
) {

    var url: String
    var plays: Int

    init {
        this.url = url
        this.plays = plays
    }

    fun increment(): Songs {
        this.plays++
        Database.statsDatabase!!.getCollection("Songs").findOneAndUpdate(
            Document("url", this.url),
            Document("\$inc", Document("plays", 1))
        )

        return this
    }

    companion object {

        fun get(url: String): Songs {

                val song = Database.statsDatabase!!.getCollection("Songs").find(Document("url", url)).firstOrNull()

                return if (song != null)
                    Songs(song.getString("url"), song.getInteger("plays"))
                else {
                    Database.statsDatabase!!.getCollection("Songs").insertOne(Document("url", url).append("plays", 1))
                    Songs(url, 1)
                }
        }

        fun countDocuments(): Long {
            return Database.statsDatabase!!.getCollection("Songs").countDocuments()
        }
    }
}