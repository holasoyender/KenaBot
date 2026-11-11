/*
 * File:          Regions.kt
 * Path:          v3\src\main\kotlin\database\schema\Regions.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-14 18:13:13 +02:00
 *
 * SHA-256: 008657c47ca4057ba9694b79caeed5e6eb38fe663bcba263664075ded68ab999
 *
 */


package database.schema

import database.Database
import org.bson.Document

class Regions(
    region: String,
    count: Int
) {

    var region: String
    var count: Int

    init {
        this.region = region
        this.count = count
    }

    @Suppress("unused")
    fun increment(): Regions {
        this.count++
        Database.statsDatabase!!.getCollection("Regions").findOneAndUpdate(
            Document("region", this.region),
            Document("\$inc", Document("count", 1))
        )

        return this
    }

    companion object {

        fun get(regionID: String): Regions {

            val region = Database.statsDatabase!!.getCollection("Regions").find(Document("region", regionID)).firstOrNull()

            return if (region != null)
                Regions(region.getString("region"), region.getInteger("count"))
            else {
                Database.statsDatabase!!.getCollection("Regions").insertOne(Document("region", regionID).append("count", 1))
                Regions(regionID, 1)
            }
        }

        @Suppress("unused")
        fun getAll(): List<Regions> {

            val regions = Database.statsDatabase!!.getCollection("Regions").find()

            return regions.toList().map {
                Regions(it.getString("region"), it.getInteger("count"))
            }

        }

        fun getTop10(): List<Regions> {
            return Database.statsDatabase!!.getCollection("Regions").find().sort(Document("count", -1)).limit(10).toList().map {
                Regions(it.getString("region"), it.getInteger("count"))
            }
        }

        fun updateFromCache(cache: MutableList<Pair<String, Int>>) {

            cache.forEach {
                val update = Database.statsDatabase!!.getCollection("Regions").findOneAndUpdate(
                    Document("region", it.first),
                    Document("\$inc", Document("count", it.second))
                )

                if (update == null) {
                    Database.statsDatabase!!.getCollection("Regions").insertOne(Document("region", it.first).append("count", it.second))
                }
            }
        }
    }
}