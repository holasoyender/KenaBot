/*
 * File:          Commands.kt
 * Path:          v3\src\main\kotlin\ws\routes\Commands.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: cc7eea7c0b1b1ee9a47fedef9b995fb938041f356facfc448c7c83f793132727
 *
 */


package ws.routes

import enums.RequestMethods
import managers.CommandManager
import okhttp3.WebSocket
import org.json.JSONArray
import org.json.JSONObject
import ws.RequestDocument

@Route("/commands")
fun commands(request: RequestDocument, ws: WebSocket) {

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

    val allCommands = CommandManager.instance!!.commands
    val commands = JSONArray()

    allCommands.forEach {

        var premium = false
        if (it.premium != null && it.premium == true)
            premium = true

        commands.put(
            JSONObject()
                .put("name", it.name)
                .put("desc", it.description)
                .put("desc_locales", it.metadata.descriptionLocalizations.toMap().map { l -> l.key.locale to l.value }.toMap())
                .put("category", it.category)
                .put("permission", it.permissions.map { p -> p.name })
                .put("premium", premium)
                .put("usage", it.usage)
        )
    }
    val response = JSONObject()
        .put("data", commands)
        .put("id", request.id)

    ws.send(response.toString())
}