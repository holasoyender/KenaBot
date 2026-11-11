/*
 * File:          Redis.kt
 * Path:          v3\src\main\kotlin\database\Redis.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-02-09 18:35:05 +01:00
 *
 * SHA-256: a1ab145281ce913c31935a059242580fb9b314826d4eb21084e95316a600ac4d
 *
 */


package database

import config.Env
import org.slf4j.LoggerFactory
import redis.clients.jedis.JedisPool

object Redis {

    var pool: JedisPool? = null
    var usingRedis = false
    var userId = ""
    private val logger = LoggerFactory.getLogger(Redis::class.java)

    init {

        try {

            val host = Env.REDIS_HOST?.split(":")?.getOrNull(0) ?: "localhost"
            val port = try { Env.REDIS_HOST?.split(":")?.getOrNull(1)?.toInt() ?: 6379 } catch (e: Exception) { 6379 }

            val conn = JedisPool(host,port)

            if (conn.resource.ping() == "PONG") {
                logger.info("Conectado con éxito a la base de datos de cache")
                pool = conn
                usingRedis = true
            } else {
                logger.error("Error al conectar con la base de datos de Redis, no se utilizará un servidor de cache.")
            }

        } catch (e: Exception) {
            logger.error("Error al conectar con la base de datos de Redis, no se utilizará un servidor de cache.")
        }
    }

    fun load() = Unit

}