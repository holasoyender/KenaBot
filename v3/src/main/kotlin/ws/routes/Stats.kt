/*
 * File:          Stats.kt
 * Path:          v3\src\main\kotlin\ws\routes\Stats.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 3ec90035f7a0230ddf02dd1388670f5c23e320439ab90346cdd88964670a881f
 *
 */


package ws.routes

import enums.RequestMethods
import functions.LastState
import okhttp3.WebSocket
import org.json.JSONObject
import ws.RequestDocument

@Route("/stats")
fun stats(request: RequestDocument, ws: WebSocket) {

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

    val shardManager = Main.shards!!
    val botGuilds = shardManager.guilds

    val topServers = shardManager.guilds.sortedByDescending { it.memberCount }.take(10)
    val servers = topServers.map { JSONObject().put("name", it.name).put("id", it.id).put("users", it.memberCount).put("image", it.iconUrl) }

    val response = JSONObject()
        .put(
            "data",
            JSONObject()
                .put("guilds", botGuilds.size)
                .put("users", botGuilds.sumOf { it.memberCount })
                .put("servers", if(shardManager.getShardById(0)!!.selfUser.name == "KenaBot") servers else listOf())
                .put("shards", shardManager.shardsTotal)
                .put("uptime", LastState.getReadyAt())
        )
        .put("id", request.id)

    ws.send(response.toString())
}