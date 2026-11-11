/*
 * File:          Claim.kt
 * Path:          v3\src\main\kotlin\ws\routes\Claim.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-05-29 13:53:04 +02:00
 *
 * SHA-256: a4fc92e3a63a335c7b0727a41f00a96ca6b618a1a06ee5d37e2c042325104012
 *
 */


package ws.routes

import database.schema.Guild
import database.schema.Premium
import enums.RequestMethods
import net.dv8tion.jda.api.Permission
import okhttp3.WebSocket
import org.json.JSONObject
import utils.Time
import ws.RequestDocument

@Route("/claim")
fun claim(request: RequestDocument, ws: WebSocket) {

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

    if (request.method != RequestMethods.POST) {
        callback(ws, request.id, "Invalid method")
        return
    }

    if (request.requestData?.params == null) {
        callback(ws, request.id, "No params found in the request")
        return
    }

    val guildId = request.requestData?.params?.firstOrNull()
    val code = request.requestData?.params?.getOrNull(1)
    if (guildId == null || code == null) {
        callback(ws, request.id, "No guild_id or code in the request")
        return
    }

    val premium = Premium.get(code)
    if (premium == null) {
        callback(ws, request.id, "That code is not valid, please contact support")
        return
    }

    if (premium.claimed) {
        callback(ws, request.id, "That code has already been claimed")
        return
    }

    if (premium.claimedAt.isNotBlank() && premium.claimedAt != guildId) {
        callback(ws, request.id, "That code is not valid for this guild")
        return
    }

    val shardManager = Main.shards!!

    val bots = premium.bots.toMutableList()
    val selfUserId = shardManager.shards.first().selfUser.id
    if (bots.contains(selfUserId)) {
        val response = JSONObject()
            .put(
                "data",
                JSONObject()
                    .put("status", 200)
                    .put("message", "Successfully claimed")
            )
            .put("id", request.id)
        ws.send(response.toString())
        return
    }

    val guild = shardManager.getGuildById(guildId)

    if (guild == null) {
        val response = JSONObject()
            .put(
                "data",
                JSONObject()
                    .put("status", 200)
                    .put("message", "The bot is not in the guild, ignoring")
            )
            .put("id", request.id)
        ws.send(response.toString())
        return
    }

    val config = Guild.get(guild.id)
    if (config.premiumEnabled) {
        if (config.premiumExpires is String) {
            callback(ws, request.id, "This guild already has premium")
            return
        }
        //if premium expires in less than 2 days throw error
        if (config.premiumExpires as Long - System.currentTimeMillis() < 172800000) {
            callback(ws, request.id, "This guild already has premium")
            return
        }
    }

    bots.add(selfUserId)
    val claimedOn = premium.claimedOn ?: System.currentTimeMillis()
    val time = Time.ms(premium.duration) + claimedOn

    if (time < System.currentTimeMillis()) {
        callback(ws, request.id, "That code is expired")
        return
    }

    premium.claimed = bots.size == 4
    premium.claimedAt = guildId
    premium.claimedOn = claimedOn
    premium.bots = bots

    premium.save()

    if (premium.duration == "Forever") {
        config.premiumEnabled = true
        config.premiumExpires = "Forever"
    } else {
        config.premiumEnabled = true
        config.premiumExpires = time
    }
    config.save()

    val response = JSONObject()
        .put(
            "data",
            JSONObject()
                .put("status", 200)
                .put("message", "Successfully claimed")
        )
        .put("id", request.id)
    ws.send(response.toString())

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