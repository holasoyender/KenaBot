/*
 * File:          Servers.kt
 * Path:          v3\src\main\kotlin\ws\routes\Servers.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-08-12 14:23:12 +02:00
 *
 * SHA-256: d34b6d839fd6f7ea867379f4dc3c8e9e36c0dbd3cff8032b480322f4a7424879
 *
 */


package ws.routes

import database.schema.Guild
import enums.RequestMethods
import langs.LangManager
import managers.CommandManager
import net.dv8tion.jda.api.Permission
import okhttp3.WebSocket
import org.json.JSONArray
import org.json.JSONObject
import ws.RequestDocument
import java.awt.Color

@Route("/servers")
fun servers(request: RequestDocument, ws: WebSocket) {

    if (request.requestData?.user == null) {
        val response = JSONObject()
            .put(
                "data",
                JSONObject()
                    .put("status", 400)
                    .put("message", "User not found")
            )
            .put("id", request.id)
        ws.send(response.toString())
        return
    }

    if (request.requestData?.params == null || request.requestData?.params?.firstOrNull() == null || request.requestData?.params?.firstOrNull() == "") {
        callback(ws, request.id)
        return
    }

    val shardManager = Main.shards!!

    val botGuilds = shardManager.guilds
    val userGuilds = request.requestData?.user?.guilds

    val mutualGuilds = userGuilds?.filter { g -> g.id in botGuilds.map { it.id } }

    val allowedGuilds = mutualGuilds?.filter {
        Permission.getPermissions(it.permissions.toLong()).contains(Permission.MANAGE_SERVER)
    }

    val guildID = request.requestData?.params?.first()

    if (allowedGuilds != null && guildID != null && guildID !in allowedGuilds.map { it.id }) {
        callback(ws, request.id)
        return
    }

    val guild = shardManager.getGuildById(guildID!!)

    if (guild == null) {
        callback(ws, request.id)
        return
    }

    when (request.method) {
        RequestMethods.GET -> {

                val rawConfig = Guild.get(guildID)

                if (rawConfig.blockedEnabled) {
                    val response = JSONObject()
                        .put(
                            "data",
                            JSONObject()
                                .put("status", 401)
                                .put("message", "Server is blocked")
                                .put("reason", rawConfig.blockedReason)
                        )
                        .put("id", request.id)
                    ws.send(response.toString())
                    return
                }

                val config = JSONObject()
                    .put(
                        "premium",
                        JSONObject()
                            .put("enabled", rawConfig.premiumEnabled)
                            .put("expires", rawConfig.premiumExpires)
                    )
                    .put(
                        "_247",
                        JSONObject()
                            .put("enabled", rawConfig.radioEnabled)
                            .put("channel", rawConfig.radioChannelID)
                    )
                    .put(
                        "disabled",
                        JSONObject()
                            .put("channels", JSONArray().putAll(rawConfig.disabledChannels))
                            .put("commands", JSONArray().putAll(rawConfig.disabledCommands))
                            .put("voice", JSONArray().putAll(rawConfig.disabledVoiceChannels))
                    )
                    .put("guildID", guildID)
                    .put("lang", rawConfig.language)
                    .put("color", colorString(rawConfig.embedColor))
                    .put("beta", rawConfig.beta)
                    .put("djRole", rawConfig.djRoleID)
                    .put("silent", rawConfig.silent)
                    .put(
                        "advanced",
                        JSONObject()
                            .put("explicitFilter", rawConfig.explicitFilter)
                            .put("maxSongDuration", rawConfig.maxSongDuration)
                            .put("autoplay", rawConfig.autoplay)
                    )

                val guildData = JSONObject()
                    .put("roles", guild.roles.map {
                        JSONObject()
                            .put("id", it.id)
                            .put("name", it.name)
                            .put("color", it.colorRaw)
                    })
                    .put("channels", guild.channels.map {
                        JSONObject()
                            .put("id", it.id)
                            .put("name", it.name)
                            .put("type", it.type.toString())
                    })
                    .put("langs", LangManager.allLangs().map {
                        JSONObject()
                            .put("flag", it.value.get("flag"))
                            .put("name", it.value.get("name"))
                            .put("description", it.value.get("description"))
                            .put("value", it.key)
                    })

                val response = JSONObject()
                    .put(
                        "data",
                        JSONObject()
                            .put("name", guild.name)
                            .put("id", guild.id)
                            .put("icon", guild.iconUrl)
                            .put("config", config)
                            .put("guild_data", guildData)
                    )
                    .put("id", request.id)
                ws.send(response.toString())
                return
        }

        RequestMethods.POST,
        RequestMethods.PUT -> {

            //second verification
            if (!allowedGuilds!!.map { it.id }.contains(guildID)) {
                callback(ws, request.id)
                return
            }

            val body = request.requestData?.body ?: ""

            if (body == "" || body.length < 10) {
                callback(ws, request.id, "Body is empty")
                return
            }

            val json = try {
                JSONObject(body)
            } catch (e: Exception) {
                callback(ws, request.id, "Body is not a valid JSON")
                return
            }

            val silent = try {
                json.getBoolean("silent")
            } catch (e: Exception) {
                return callback(ws, request.id, "No 'silent' field (boolean)")
            }
            val beta = try {
                json.getBoolean("beta")
            } catch (e: Exception) {
                return callback(ws, request.id, "No 'beta' field (boolean)")
            }
            val color = try {
                json.getString("color")
            } catch (e: Exception) {
                return callback(ws, request.id, "No 'color' field (string)")
            }
            if (!validColor(color)) {
                return callback(ws, request.id, "Invalid color: '$color'")
            }

            val lang = try {
                json.getString("lang")
            } catch (e: Exception) {
                return callback(ws, request.id, "No 'lang' field (string)")
            }
            if (!LangManager.langNames.contains(lang)) {
                return callback(ws, request.id, "Invalid lang: '$lang'")
            }

            val djRole = try {
                json.getString("djRole")
            } catch (e: Exception) {
                return callback(ws, request.id, "No 'djRole' field (string)")
            }
            if (djRole != "null" && djRole.isNotBlank()) {
                try {
                    guild.getRoleById(djRole) ?: return callback(ws, request.id, "Invalid djRole: '$djRole'")
                } catch (e: Exception) {
                    return callback(ws, request.id, "Invalid djRole: '$djRole'")
                }
            }

            val disabled = try {
                json.getJSONObject("disabled")
            } catch (e: Exception) {
                return callback(ws, request.id, "No 'disabled' field (object)")
            }
            val disabledChannels = try {
                disabled.getJSONArray("channels")
            } catch (e: Exception) {
                return callback(ws, request.id, "No 'disabled.channels' field (array)")
            }
            if (disabledChannels.length() > 0) {
                disabledChannels.forEach {
                    try {
                        guild.getTextChannelById(it.toString()) ?: return callback(
                            ws,
                            request.id,
                            "Invalid disabled.channels: '$it'"
                        )
                    } catch (e: Exception) {
                        return callback(ws, request.id, "Invalid disabled.channels: '$it'")
                    }
                }
            }
            val disabledCommands = try {
                disabled.getJSONArray("commands")
            } catch (e: Exception) {
                return callback(ws, request.id, "No 'disabled.commands' field (array)")
            }
            if (disabledCommands.length() > 0) {
                disabledCommands.forEach {
                    if (!CommandManager.instance!!.commands.map { c -> c.name }.contains(it.toString())) {
                        return callback(ws, request.id, "Invalid disabled.commands: '$it'")
                    }
                }
            }
            val disabledVoiceChannels = try {
                disabled.getJSONArray("voice")
            } catch (e: Exception) {
                return callback(ws, request.id, "No 'disabled.voice' field (array)")
            }
            if (disabledVoiceChannels.length() > 0) {
                disabledVoiceChannels.forEach {
                    try {
                        guild.getVoiceChannelById(it.toString()) ?: guild.getStageChannelById(it.toString()) ?: return callback(
                            ws,
                            request.id,
                            "Invalid disabled.voice: '$it'"
                        )
                    } catch (e: Exception) {
                        return callback(ws, request.id, "Invalid disabled.voice: '$it'")
                    }
                }
            }

            val advanced = try {
                json.getJSONObject("advanced")
            } catch (e: Exception) {
                return callback(ws, request.id, "No 'advanced' field (object)")
            }
            val explicitFilter = try {
                advanced.getBoolean("explicitFilter")
            } catch (e: Exception) {
                return callback(ws, request.id, "No 'advanced.explicitFilter' field (boolean)")
            }
            val maxSongDuration = try {
                advanced.getInt("maxSongDuration")
            } catch (e: Exception) {
                return callback(ws, request.id, "No 'advanced.maxSongDuration' field (int)")
            }
            if (maxSongDuration < 0) {
                return callback(ws, request.id, "Invalid advanced.maxSongDuration: '$maxSongDuration'")
            }
            val autoplay = try {
                advanced.getBoolean("autoplay")
            } catch (e: Exception) {
                return callback(ws, request.id, "No 'advanced.autoplay' field (boolean)")
            }

            val _247 = try {
                json.getJSONObject("_247")
            } catch (e: Exception) {
                return callback(ws, request.id, "No '_247' field (object)")
            }
            val _247Enabled = try {
                _247.getBoolean("enabled")
            } catch (e: Exception) {
                return callback(ws, request.id, "No '_247.enabled' field (boolean)")
            }
            val _247Channel = try {
                _247.getString("channel")
            } catch (e: Exception) {
                return callback(ws, request.id, "No '_247.channel' field (string)")
            }
            if (_247Enabled) {
                if (_247Channel == "null" || _247Channel.isBlank()) {
                    return callback(ws, request.id, "Invalid _247.channel: '$_247Channel'")
                }
                try {
                    guild.getVoiceChannelById(_247Channel) ?: guild.getStageChannelById(_247Channel) ?: return callback(
                        ws,
                        request.id,
                        "Invalid _247.channel: '$_247Channel'"
                    )
                } catch (e: Exception) {
                    return callback(ws, request.id, "Invalid _247.channel: '$_247Channel'")
                }
            }

            val config = Guild.get(guild.id)
            config.silent = silent
            config.beta = beta
            config.embedColor = Color.decode(color)
            config.language = lang
            config.djRoleID = djRole
            config.disabledChannels = disabledChannels.toList().map { it.toString() }
            config.disabledCommands = disabledCommands.toList().map { it.toString() }
            config.disabledVoiceChannels = disabledVoiceChannels.toList().map { it.toString() }
            config.explicitFilter = explicitFilter
            config.maxSongDuration = try { maxSongDuration.toLong() } catch (e: Exception) { 0 }
            config.autoplay = autoplay
            config.radioEnabled = _247Enabled
            config.radioChannelID = _247Channel

            config.save()

            val response = JSONObject()
                .put(
                    "data",
                    JSONObject()
                        .put("status", 200)
                        .put("message", "Successfully updated config")
                )
                .put("id", request.id)
            ws.send(response.toString())

            return
        }

        else -> {
            callback(ws, request.id)
            return
        }
    }
}

private fun callback(ws: WebSocket, id: String, error: String = "Server is unavailable") {
    val response = JSONObject()
        .put(
            "data",
            JSONObject()
                .put("status", 401)
                .put("message", error)
        )
        .put("id", id)
    ws.send(response.toString())
    return
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

fun validColor(color: String): Boolean {
    return try {
        Color.decode(color)
        true
    } catch (e: Exception) {
        false
    }
}