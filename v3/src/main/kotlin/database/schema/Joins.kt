/*
 * File:          Joins.kt
 * Path:          v3\src\main\kotlin\database\schema\Joins.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 0dc64551daf56dc7173cc7082c88b7f81bfaf729b8eabf4a8b181570cdb911fa
 *
 */


package database.schema

import database.Database
import org.bson.Document

class Joins(
    channelID: String,
    joins: Int
) {

    var channelID: String
    var joins: Int

    init {
        this.channelID = channelID
        this.joins = joins
    }

    fun increment(): Joins {
        this.joins++
        Database.statsDatabase!!.getCollection("Joins").findOneAndUpdate(
            Document("channelID", this.channelID),
            Document("\$inc", Document("joins", 1))
        )

        return this
    }


    companion object {

        fun get(channelID: String): Joins {

            val join = Database.statsDatabase!!.getCollection("Joins").find(Document("channelID", channelID)).firstOrNull()

            return if (join != null)
                Joins(join.getString("channelID"), join.getInteger("joins"))
            else {
                Database.statsDatabase!!.getCollection("Joins").insertOne(Document("channelID", channelID).append("joins", 1))
                Joins(channelID, 1)
            }
        }

        @Suppress("unused")
        fun getAll(): List<Joins> {

            val joins = Database.statsDatabase!!.getCollection("Joins").find()

            return joins.toList().map {
                Joins(it.getString("channelID"), it.getInteger("joins"))
            }

        }

        fun getTop10(): List<Joins> {
            return Database.statsDatabase!!.getCollection("Joins").find().sort(Document("joins", -1)).limit(10).toList().map {
                Joins(it.getString("channelID"), it.getInteger("joins"))
            }
        }

    }
}