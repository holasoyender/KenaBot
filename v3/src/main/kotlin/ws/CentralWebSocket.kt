/*
 * File:          CentralWebSocket.kt
 * Path:          v3\src\main\kotlin\ws\CentralWebSocket.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-20 12:50:01 +01:00
 *
 * SHA-256: 4778c9bcd3ae6fe7d06bbf87479c47a25193d2f7797fcb752590190c957330eb
 *
 */


package ws

import config.Env
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

object CentralWebSocket {

    var websocket: WebSocket? = null
    private var aknowledged = false
    private var reconnectTries = 0
    var thread: Thread? = null

    private val logger = LoggerFactory.getLogger("WebSocket")

    fun load(clientName: String) {

        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        val request: Request = Request.Builder()
            .url("ws://${Env.CENTRAL_API_URL ?: "localhost:4000"}")
            .addHeader("Authorization", "56bad67b2ae2408ba54470b0e75a7605")
            .addHeader("Client-Name", clientName)
            .build()

        websocket = client.newWebSocket(request, Listener(clientName))

        client.dispatcher.executorService.shutdown()
    }

    fun startReconnectLoop(clientName: String) {
        if (!aknowledged) {
            aknowledged = true
            thread = Thread {
                while (true) {
                    if(reconnectTries == 0)
                        logger.info("Parece que la conexión con el WebSocket se ha perdido, reintentando cada 10 segundos...")

                    reconnectTries++
                    load(clientName)
                    Thread.sleep(10000)
                }
            }
            thread?.isDaemon = true
            thread?.name = "WebSocketReconnectService"
            thread?.start()
        }
    }

    fun acknowledge() {
        try { thread?.interrupt() } catch (_: InterruptedException) {}
        thread = null

        aknowledged = false
        reconnectTries = 0
    }
}