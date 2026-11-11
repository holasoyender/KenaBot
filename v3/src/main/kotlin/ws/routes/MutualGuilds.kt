/*
 * File:          MutualGuilds.kt
 * Path:          v3\src\main\kotlin\ws\routes\MutualGuilds.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: f5687005ebdb54cae71945b975d79f5cfe4f21253258ae3aab9ad6bd479899a7
 *
 */


package ws.routes

import enums.RequestMethods
import okhttp3.WebSocket
import org.json.JSONArray
import org.json.JSONObject
import ws.RequestDocument

@Route("/mutualguilds")
fun mutualGuilds(request: RequestDocument, ws: WebSocket) {

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

    if(request.requestData?.user == null) {
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

    val botGuilds = Main.shards!!.guilds
    val userGuilds = request.requestData?.user?.guilds

    val mutualGuilds = userGuilds?.filter { g -> g.id in botGuilds.map { it.id } }
    val guilds = JSONArray()

    mutualGuilds?.forEach { g ->
        guilds.put(
            JSONObject()
                .put("id", g.id)
                .put("name", g.name)
                .put("icon", g.icon)
                .put("owner", g.owner)
                .put("permission", g.permissions)
        )
    }
    val response = JSONObject()
        .put(
            "data",
            JSONObject()
                .put("status", 200)
                .put("data", guilds)
        )
        .put("id", request.id)

    ws.send(response.toString())
}