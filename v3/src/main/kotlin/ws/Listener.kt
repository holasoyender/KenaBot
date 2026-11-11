/*
 * File:          Listener.kt
 * Path:          v3\src\main\kotlin\ws\Listener.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-05-01 18:14:01 +02:00
 *
 * SHA-256: 5df4c22415cb61bbb93659a1084486e649f6b0d4e561b88a5ff68e42c162737a
 *
 */


package ws

import enums.RequestTypes
import io.sentry.Sentry
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import org.slf4j.LoggerFactory
import ws.routes.*

class Listener(private val clientName: String) : WebSocketListener() {

    private val logger = LoggerFactory.getLogger("WebSocket")

    override fun onOpen(webSocket: WebSocket, response: Response) {
        logger.info("Conectado al WebSocket de la API central")
        CentralWebSocket.acknowledge()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        var json: JSONObject? = null
        var request: RequestDocument? = null
        try {
            json = JSONObject(text)
        } catch (e: Exception) {
            logger.info("Error al parsear el JSON")
            e.printStackTrace()
            Sentry.captureException(e)
        }

        if(json != null) {
            request = RequestDocument(json)
        }

        if(request != null && request.type == RequestTypes.ACTION) {
            when(request.action) {
                "/commands" -> commands(request, webSocket)
                "/stats" -> stats(request, webSocket)
                "/status" -> status(request, webSocket)
                "/langs" -> langs(request, webSocket)
                "/mutualguilds" -> mutualGuilds(request, webSocket)
                "/servers" -> servers(request, webSocket)
                "/blocked" -> blocked(request, webSocket)
                "/claim" -> claim(request, webSocket)
            }
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        CentralWebSocket.startReconnectLoop(clientName)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        if(t.message?.contains("Failed to connect to localhost") == false)
            println("Fallo la conexión, ${t.message}")
        CentralWebSocket.startReconnectLoop(clientName)
    }
}