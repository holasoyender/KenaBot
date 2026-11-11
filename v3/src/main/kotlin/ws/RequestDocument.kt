/*
 * File:          RequestDocument.kt
 * Path:          v3\src\main\kotlin\ws\RequestDocument.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-06-29 21:43:39 +02:00
 *
 * SHA-256: 6804676752bd6f39c41dc3bbf78a93baa80f13d2af7aeeec67b4c7cd8d4ccabd
 *
 */


package ws

import enums.*
import org.json.JSONObject

class RequestDocument(
    request: JSONObject
) {

    var type: RequestTypes = RequestTypes.UNKNOWN
    var action: String = "unknown"
    var method: RequestMethods = RequestMethods.UNKNOWN
    var id: String = ""
    var requestData: Data? = null

    init {

        if (request.has("type")) {
            type = when (request.getString("type")) {
                "action" -> RequestTypes.ACTION
                "response" -> RequestTypes.RESPONSE
                else -> RequestTypes.UNKNOWN
            }
        }
        if (request.has("action")) {
            action = request.getString("action")
        }
        if (request.has("method")) {
            method = when (request.getString("method")) {
                "GET" -> RequestMethods.GET
                "POST" -> RequestMethods.POST
                "PUT" -> RequestMethods.PUT
                "DELETE" -> RequestMethods.DELETE
                else -> RequestMethods.UNKNOWN
            }
        }
        if (request.has("id")) {
            id = request.getString("id")
        }
        if (request.has("data")) {
            val data = request.getJSONObject("data")

            val paramsJSON = data.getJSONArray("params")
            val params: ArrayList<String> = ArrayList()
            if (paramsJSON != null) {
                for (i in 0 until paramsJSON.length()) {
                    params.add(paramsJSON.getString(i))
                }
            }

            val body = if (data.has("body")) try {
                data.getString("body")
            } catch (_: Exception) {
                try {
                    data.getJSONObject("body").toString()
                } catch (_: Exception) {
                    null
                }
            } else null

            if (data.has("user") && data.get("user") != JSONObject.NULL) {
                val userJSON = data.getJSONObject("user")
                var user: ApiUser? = null
                if (userJSON != null) {
                    var id: String? = null
                    var username: String? = null
                    var avatar: String? = null
                    val guilds: ArrayList<ApiGuild> = ArrayList()

                    if (userJSON.has("id")) {
                        id = userJSON.getString("id")
                    }
                    if (userJSON.has("username")) {
                        username = userJSON.getString("username")
                    }
                    if (userJSON.has("avatar")) {
                        avatar = userJSON.getString("avatar")
                    }
                    if (userJSON.has("guilds")) {
                        val guildsJSON = userJSON.getJSONArray("guilds")
                        for (i in 0 until guildsJSON.length()) {
                            val guildJSON = guildsJSON.getJSONObject(i)
                            val guild = ApiGuild(
                                if (guildJSON.has("id") && guildJSON.get("id") is String) guildJSON.getString("id") else "0",
                                if (guildJSON.has("name") && guildJSON.get("name") is String) guildJSON.getString("name") else "Unknown",
                                if (guildJSON.has("icon") && guildJSON.get("icon") is String) guildJSON.getString("icon") else "https://cdn.discordapp.com/embed/avatars/0.png",
                                if (guildJSON.has("owner") && guildJSON.get("owner") is Boolean) guildJSON.getBoolean("owner") else false,
                                if (guildJSON.has("permissions") && guildJSON.get("permissions") is Int) guildJSON.getInt("permissions") else 0
                            )
                            guilds.add(guild)
                        }
                    }
                    user = ApiUser(
                        id ?: "unknown",
                        username ?: "unknown",
                        avatar ?: "https://cdn.discordapp.com/embed/avatars/0.png",
                        guilds
                    )



                }
                requestData = Data(user, params, body)

            } else {
                requestData = Data(null, params, body)
            }
        }
    }

    override fun toString(): String {
        return "RequestDocument(type=$type, action='$action', method=$method, id='$id', requestData=$requestData)"
    }
}