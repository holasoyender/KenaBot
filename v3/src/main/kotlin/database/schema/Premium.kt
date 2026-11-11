/*
 * File:          Premium.kt
 * Path:          v3\src\main\kotlin\database\schema\Premium.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-02-07 23:06:01 +01:00
 *
 * SHA-256: acbabe408db19355a957855cf2cbf294adf267fabf0f9129bc78c06ffece6110
 *
 */


package database.schema

import database.Database
import org.bson.Document

class Premium(
    code: String,
    duration: String,
    createdAt: Long,
    claimed: Boolean,
    claimedAt: String,
    claimedOn: Long?,
    createdBy: String,
    bots: List<String>
) {

    var code: String
    var duration: String
    var createdAt: Long
    var claimed: Boolean
    var claimedAt: String
    var claimedOn: Long?
    var createdBy: String
    var bots: List<String>

    init {
        this.code = code
        this.duration = duration
        this.createdAt = createdAt
        this.claimed = claimed
        this.claimedAt = claimedAt
        this.claimedOn = claimedOn
        this.createdBy = createdBy
        this.bots = bots
    }

    fun save(): Premium {

        val document = Document()

        document["code"] = code
        document["duration"] = duration
        document["createdAt"] = createdAt
        document["claimed"] = claimed
        document["claimedAt"] = claimedAt
        document["claimedOn"] = claimedOn
        document["createdBy"] = createdBy
        document["bots"] = bots

        if(exists()) {
            Database.statsDatabase!!.getCollection("Premiums").findOneAndUpdate(Document("code", code), Document("\$set", document))
        } else {
            Database.statsDatabase!!.getCollection("Premiums").insertOne(document)
        }

        return this
    }

    fun delete() {
        Database.statsDatabase!!.getCollection("Premiums").findOneAndDelete(Document("code", code))
    }

    private fun exists(): Boolean {
        return Database.statsDatabase!!.getCollection("Premiums").find(Document("code", code)).firstOrNull() != null
    }

    companion object {

        fun get(code: String): Premium? {
            val document = Database.statsDatabase!!.getCollection("Premiums").find(Document("code", code)).firstOrNull()

            return if(document != null) {
                Premium(
                    document.getString("code"),
                    document.getString("duration"),
                    try { document.getLong("createdAt") } catch (_: Exception) { try { document.getDate("createdAt").time } catch (_: Exception) { 0L } },
                    document.getBoolean("claimed"),
                    document.getString("claimedAt"),
                    document.getLong("claimedOn"),
                    document.getString("createdBy"),
                    document.getList("bots", String::class.java)
                )
            } else {
                null
            }
        }
    }
}