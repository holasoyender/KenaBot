/*
 * File:          Blocked.kt
 * Path:          v3\src\main\kotlin\ws\routes\Blocked.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-02-07 23:06:01 +01:00
 *
 * SHA-256: 93e4831d6a9a32b67b9ea0e473804bae0e331b6c613b848e97987386f6d8a613
 *
 */


package ws.routes

import database.schema.Guild
import enums.RequestMethods
import okhttp3.WebSocket
import org.json.JSONArray
import org.json.JSONObject
import ws.RequestDocument

@Route("/blocked")
fun blocked(request: RequestDocument, ws: WebSocket) {

    if (request.method != RequestMethods.GET) {
        val response = JSONObject()
            .put(
                "data",
                JSONObject()
                    .put("status", 400)
                    .put("message", "Method not allowed")
            )
            .put("id", request.id)
        ws.send(response.toString())
        return
    }

    val guildId = request.requestData?.params?.firstOrNull()

    if(guildId == null) {
        val response = JSONObject()
            .put(
                "data",
                JSONObject()
                    .put("status", 400)
                    .put("message", "Guild not found")
            )
            .put("id", request.id)
        ws.send(response.toString())
        return
    }

    val config = Guild.get(guildId)
    val urls = config.blockedSongs.map { "https://$it" }
    val response = JSONObject()
        .put(
            "data",
            JSONArray(urls)
        )
        .put("id", request.id)

    ws.send(response.toString())
}