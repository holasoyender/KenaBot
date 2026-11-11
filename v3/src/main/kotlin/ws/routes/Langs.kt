/*
 * File:          Langs.kt
 * Path:          v3\src\main\kotlin\ws\routes\Langs.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 49affb8a8d0176e33eb9b8e1df220ccc69737d7261dd82f313394cd8e34b6dfb
 *
 */


package ws.routes

import enums.RequestMethods
import langs.LangManager
import okhttp3.WebSocket
import org.json.JSONArray
import org.json.JSONObject
import ws.RequestDocument

@Route("/langs")
fun langs(request: RequestDocument, ws: WebSocket) {

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

    val allLangs = LangManager.allLangs()
    val langs = JSONArray()
    allLangs.forEach {

        langs.put(
            JSONObject()
                .put("flag", it.value.get("flag"))
                .put("name", it.value.get("name"))
                .put("description", it.value.get("description"))
        )
    }
    val response = JSONObject()
        .put("data", langs)
        .put("id", request.id)

    ws.send(response.toString())
}