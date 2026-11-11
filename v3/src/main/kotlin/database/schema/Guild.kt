/*
 * File:          Guild.kt
 * Path:          v3\src\main\kotlin\database\schema\Guild.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: c378bdb070e6187a40f2b6effcd52528153d4981f6895d26275b9967706c1863
 *
 */


package database.schema

import com.mongodb.client.MongoCollection
import database.Database
import org.bson.Document
import java.awt.Color

class Guild(
    guildID: String,
    language: String,
    embedColor: Color,

    radioChannelID: String,
    radioEnabled: Boolean,

    premiumExpires: Any,
    premiumEnabled: Boolean,

    blockedReason: String,
    blockedEnabled: Boolean,

    disabledChannels: List<String>,
    disabledCommands: List<String>,
    disabledVoiceChannels: List<String>,

    beta: Boolean,
    silent: Boolean,
    djRoleID: String,
    autoplay: Boolean,

    maxSongDuration: Long,
    blockedSongs: List<String>,
    explicitFilter: Boolean,

    found: Boolean,
) {

    var guildID: String
    var language: String
    var embedColor: Color

    var radioChannelID: String
    var radioEnabled: Boolean

    var premiumExpires: Any
    var premiumEnabled: Boolean

    var blockedReason: String
    var blockedEnabled: Boolean

    var disabledChannels: List<String>
    var disabledCommands: List<String>
    var disabledVoiceChannels: List<String>

    var beta: Boolean
    var silent: Boolean
    var djRoleID: String
    var autoplay: Boolean

    var maxSongDuration: Long
    var blockedSongs: List<String>
    var explicitFilter: Boolean

    var found: Boolean

    init {
        this.guildID = guildID
        this.language = language
        this.embedColor = embedColor

        this.radioChannelID = radioChannelID
        this.radioEnabled = radioEnabled

        this.premiumExpires = premiumExpires
        this.premiumEnabled = premiumEnabled

        this.blockedReason = blockedReason
        this.blockedEnabled = blockedEnabled

        this.disabledChannels = disabledChannels
        this.disabledCommands = disabledCommands
        this.disabledVoiceChannels = disabledVoiceChannels

        this.beta = beta
        this.silent = silent
        this.djRoleID = djRoleID
        this.autoplay = autoplay

        this.maxSongDuration = maxSongDuration
        this.blockedSongs = blockedSongs
        this.explicitFilter = explicitFilter

        this.found = found

    }

    fun save(): Guild {

        if(guildID.isBlank()) return this

        val document = Document()
        document["guildID"] = guildID
        document["lang"] = language
        document["color"] = colorString(embedColor)

        document["premium"] = Document()
            .append("expires", premiumExpires)
            .append("enabled", premiumEnabled)

        document["_247"] = Document()
            .append("channelID", radioChannelID)
            .append("enabled", radioEnabled)

        document["blocked"] = Document()
            .append("reason", blockedReason)
            .append("enabled", blockedEnabled)

        document["disabled"] = Document()
            .append("channels", disabledChannels)
            .append("commands", disabledCommands)
            .append("voice", disabledVoiceChannels)

        document["advanced"] = Document()
            .append("maxSongDuration", maxSongDuration)
            .append("blockedSongs", blockedSongs)
            .append("explicitFilter", explicitFilter)

        document["beta"] = beta
        document["silent"] = silent
        document["dj_role"] = djRoleID
        document["autoplay"] = autoplay

        if(exists()) {
            Database.mainDatabase!!.getCollection("Guilds").findOneAndUpdate(Document("guildID", guildID), Document("\$set", document))
        } else {
            Database.mainDatabase!!.getCollection("Guilds").insertOne(document)
        }

        /*if(Redis.usingRedis)
            try {
                Redis.pool!!.resource.setex("${Redis.userId}:guilds:$guildID", 3600, document.toJson())
            } catch (_: Exception) {}*/

        this.found = true
        return this
    }

    private fun exists(): Boolean {
        return Database.mainDatabase!!.getCollection("Guilds").find(Document("guildID", this.guildID)).firstOrNull() != null
    }

    companion object {

        fun get(id: String/*, force: Boolean = false*/): Guild {

            /*if (Redis.usingRedis)
                if (!force) {
                    try {
                        val cache = Redis.pool!!.resource.get("${Redis.userId}:guilds:$id")
                        if (cache != null && cache != "OK") {
                            return formatDocument(Document.parse(cache))
                        }
                    } catch (e: Exception) {
                        return get(id, true)
                    }
                }*/

            val document = Database.mainDatabase!!.getCollection("Guilds").find(Document("guildID", id)).firstOrNull()

            return if (document != null) {
                //if (Redis.usingRedis)
                //    Redis.pool!!.resource.setex("${Redis.userId}:guilds:$id", 3600, document.toJson())

                return formatDocument(document)
            } else {
                defaultConfig(id)
            }
        }

        @Suppress("unused")
        fun getAll(): List<Guild> {
            val documents = Database.mainDatabase!!.getCollection("Guilds").find().toList()
            return documents.map { formatDocument(it) }
        }

        fun getAllPremium(): List<Guild> {
            val documents = Database.mainDatabase!!.getCollection("Guilds").find(Document("premium.enabled", true)).toList()
            return documents.map { formatDocument(it) }
        }

        fun getCollection(): MongoCollection<Document> {
            return Database.mainDatabase!!.getCollection("Guilds")
        }


        fun formatDocument(document: Document): Guild {
            return Guild(
                document.getString("guildID") ?: "",
                document.getString("lang") ?: "es-ES",
                Color.decode(document.getString("color") ?: "#2f3136") ?: Color.decode("#2f3136"),
                try {
                    (document["_247"] as Document).getString("channelID") ?: ""
                } catch (e: Exception) {
                    ""
                },
                try {
                    (document["_247"] as Document).getBoolean("enabled") ?: false
                } catch (e: Exception) {
                    false
                },
                try {
                    (document["premium"] as Document).getLong("expires")
                } catch (e: Exception) {
                    try {
                        (document["premium"] as Document).getDouble("expires")
                    } catch (e: Exception) {
                        (document["premium"] as Document).getString("expires")
                    }
                },
                try {
                    (document["premium"] as Document).getBoolean("enabled") ?: false
                } catch (e: Exception) {
                    false
                },
                try {
                    (document["blocked"] as Document).getString("reason") ?: ""
                } catch (e: Exception) {
                    ""
                },
                try {
                    (document["blocked"] as Document).getBoolean("enabled") ?: false
                } catch (e: Exception) {
                    false
                },
                try {
                    (document["disabled"] as Document).getList("channels", String::class.java)
                } catch (e: Exception) {
                    listOf()
                },
                try {
                    (document["disabled"] as Document).getList("commands", String::class.java)
                } catch (e: Exception) {
                    listOf()
                },
                try {
                    (document["disabled"] as Document).getList("voice", String::class.java)
                } catch (e: Exception) {
                    listOf()
                },
                try{
                    document.getBoolean("beta") ?: false
                } catch (e: Exception) {
                    false
                },
                try{
                    document.getBoolean("silent") ?: false
                } catch (e: Exception) {
                    false
                },
                try {
                    document.getString("dj_role")
                } catch (e: Exception) {
                    ""
                },
                try {
                    document.getBoolean("autoplay") ?: false
                } catch (e: Exception) {
                    false
                },
                try {
                    (document["advanced"] as Document).getLong("maxSongDuration") ?: 0
                } catch (e: Exception) {
                    0
                },
                try {
                    (document["advanced"] as Document).getList("blockedSongs", String::class.java) ?: listOf()
                } catch (e: Exception) {
                    listOf()
                },
                try {
                    (document["advanced"] as Document).getBoolean("explicitFilter") ?: false
                } catch (e: Exception) {
                    false
                },
                true
            )
        }

        fun defaultConfig(): Guild {
            return Guild(
                "",
                "es-ES",
                Color.decode("#2f3136"),
                "",
                false,
                "",
                false,
                "",
                false,
                listOf(),
                listOf(),
                listOf(),
                beta = false,
                silent = false,
                djRoleID = "",
                false,
                0,
                listOf(),
                false,
                found = false
            )
        }

        fun defaultConfig(guildID: String): Guild {
            return Guild(
                guildID,
                "es-ES",
                Color.decode("#2f3136"),
                "",
                false,
                "",
                false,
                "",
                false,
                listOf(),
                listOf(),
                listOf(),
                beta = false,
                silent = false,
                djRoleID = "",
                false,
                0,
                listOf(),
                false,
                found = false
            )
        }

        private fun colorString(color: Color): String {
            val red = Integer.toHexString(color.red)
            val green = Integer.toHexString(color.green)
            val blue = Integer.toHexString(color.blue)
            return "#" +
                    (if (red.length == 1) "0$red" else red) +
                    (if (green.length == 1) "0$green" else green) +
                    if (blue.length == 1) "0$blue" else blue
        }
    }

}