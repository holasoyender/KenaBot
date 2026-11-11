/*
 * File:          Status.kt
 * Path:          v3\src\main\kotlin\ws\routes\Status.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-02-07 23:06:01 +01:00
 *
 * SHA-256: 0b7ccf06eec2578128815c91bc089516281f00a466b675506f846af167ad7ff6
 *
 */


package ws.routes

import enums.RequestMethods
import functions.LastState
import okhttp3.WebSocket
import org.json.JSONArray
import org.json.JSONObject
import ws.RequestDocument

@Route("/status")
fun status(request: RequestDocument, ws: WebSocket) {

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
    val shards = JSONArray()
    var shardCount = 0

    for (shard in shardManager.shards) {

        shardCount++
        val shardData = JSONObject()
            .put("id", shardCount)
            .put("host-name", "shard-${shardCount}")
            .put("gatewayState", shard.status.toString())
            .put("discordState", LastState.getLastState())
            .put("latency", shard.gatewayPing)
            .put("guilds", shard.guilds.size)
            .put("users", shard.guilds.sumOf { it.memberCount })
            .put("unavailableGuilds", shard.unavailableGuilds)
        shards.put(shardData)

    }

    val response = JSONObject()
        .put(
            "data",
            shards
        )
        .put("id", request.id)

    ws.send(response.toString())
}