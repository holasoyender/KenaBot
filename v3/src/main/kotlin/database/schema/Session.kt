/*
 * File:          Session.kt
 * Path:          v3\src\main\kotlin\database\schema\Session.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 349eb16f829a417db0fc26f50a658c1ddbc84822d666ab95c57f011721772308
 *
 */


package database.schema

import database.Database
import enums.TogetherListener
import org.bson.Document

class Session(
    sourceGuildId: String,
    sourceBotName: String,
    inviteCode: String,
    createdAt: Long,
    listeners: List<TogetherListener>
) {

    val sourceGuildId: String
    val sourceBotName: String
    val inviteCode: String
    val createdAt: Long
    var listeners: List<TogetherListener>

    init {
        this.sourceGuildId = sourceGuildId
        this.sourceBotName = sourceBotName
        this.inviteCode = inviteCode
        this.createdAt = createdAt
        this.listeners = listeners
    }

    fun save(): Session {

        val document = Document()

        document["sourceGuildId"] = sourceGuildId
        document["sourceBotName"] = sourceBotName
        document["inviteCode"] = inviteCode
        document["createdAt"] = createdAt
        document["listeners"] = listeners.map {
            Document()
                .append("guildId", it.guildId)
                .append("botName", it.botName)
                .append("channelId", it.channelId)
        }

        if(exists()) {
            Database.statsDatabase!!.getCollection("Sessions").findOneAndUpdate(Document("inviteCode", inviteCode), Document("\$set", document))
        } else {
            Database.statsDatabase!!.getCollection("Sessions").insertOne(document)
        }

        return this
    }

    fun delete() {
        Database.statsDatabase!!.getCollection("Sessions").findOneAndDelete(Document("inviteCode", inviteCode))
    }

    fun join(guildId: String, botName: String, channelId: String): Session {

        val listener = TogetherListener(guildId, botName, channelId)
        val currentListeners = listeners.toMutableList()

        if(!currentListeners.contains(listener)) {
            currentListeners.add(listener)
            listeners = currentListeners.toList()
            save()
        }

        return this
    }

    fun leave(guildId: String, botName: String): Session {

        val currentListeners = listeners.toMutableList()
        val listener = currentListeners.firstOrNull { it.guildId == guildId && it.botName == botName }

        if(listener != null) {
            currentListeners.remove(listener)
            listeners = currentListeners.toList()
            save()
        }

        return this
    }

    private fun exists(): Boolean {
        return Database.statsDatabase!!.getCollection("Sessions").find(Document("inviteCode", inviteCode)).firstOrNull() != null
    }

    companion object {

        fun get(inviteCode: String): Session? {
            val session = Database.statsDatabase!!.getCollection("Sessions").find(Document("inviteCode", inviteCode)).firstOrNull()

            return if(session != null) {
                Session(
                    session.getString("sourceGuildId"),
                    session.getString("sourceBotName"),
                    session.getString("inviteCode"),
                    session.getLong("createdAt"),
                    session.getList("listeners", Document::class.java).map {
                        TogetherListener(
                            it.getString("guildId"),
                            it.getString("botName"),
                            it.getString("channelId")
                        )
                    }
                )
            } else null
        }

        fun get(guildId: String, botName: String, asListener: Boolean): Session? {

            val session = Database.statsDatabase!!.getCollection("Sessions").find(
                if(asListener) {
                    Document("listeners", Document("\$elemMatch", Document("guildId", guildId).append("botName", botName)))
                } else {
                    Document("sourceGuildId", guildId).append("sourceBotName", botName)
                }
            ).firstOrNull()

            return if(session != null) {
                Session(
                    session.getString("sourceGuildId"),
                    session.getString("sourceBotName"),
                    session.getString("inviteCode"),
                    session.getLong("createdAt"),
                    session.getList("listeners", Document::class.java).map {
                        TogetherListener(
                            it.getString("guildId"),
                            it.getString("botName"),
                            it.getString("channelId"),
                        )
                    }
                )
            } else null
        }

        fun get(guildId: String, botName: String): Pair<Boolean, Session?> {

            val sessionAsHost = Database.statsDatabase!!.getCollection("Sessions").find(
                Document("sourceGuildId", guildId).append("sourceBotName", botName)
            ).firstOrNull()

            if(sessionAsHost != null) {
                return Pair(true, Session(
                    sessionAsHost.getString("sourceGuildId"),
                    sessionAsHost.getString("sourceBotName"),
                    sessionAsHost.getString("inviteCode"),
                    sessionAsHost.getLong("createdAt"),
                    sessionAsHost.getList("listeners", Document::class.java).map {
                        TogetherListener(
                            it.getString("guildId"),
                            it.getString("botName"),
                            it.getString("channelId"),
                        )
                    }
                ))
            } else {
                val sessionAsListener = Database.statsDatabase!!.getCollection("Sessions").find(
                    Document("listeners", Document("\$elemMatch", Document("guildId", guildId).append("botName", botName)))
                ).firstOrNull()

                if(sessionAsListener != null) {
                    return Pair(false, Session(
                        sessionAsListener.getString("sourceGuildId"),
                        sessionAsListener.getString("sourceBotName"),
                        sessionAsListener.getString("inviteCode"),
                        sessionAsListener.getLong("createdAt"),
                        sessionAsListener.getList("listeners", Document::class.java).map {
                            TogetherListener(
                                it.getString("guildId"),
                                it.getString("botName"),
                                it.getString("channelId"),
                            )
                        }
                    ))
                } else {
                    return Pair(false, null)
                }
            }
        }

        fun getAll(): List<Session> {
            return Database.statsDatabase!!.getCollection("Sessions").find().map { document ->
                Session(
                    document.getString("sourceGuildId"),
                    document.getString("sourceBotName"),
                    document.getString("inviteCode"),
                    document.getLong("createdAt"),
                    document.getList("listeners", Document::class.java).map {
                        TogetherListener(
                            it.getString("guildId"),
                            it.getString("botName"),
                            it.getString("channelId"),
                        )
                    }
                )
            }.toList()
        }
        fun generateCode(): String {

            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            val code = StringBuilder()

            for (i in 0..8) {
                val random = (Math.random() * chars.length).toInt()
                code.append(chars[random])
            }

            return code.toString()
        }
    }

}