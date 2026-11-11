/*
 * File:          RotatingKeys.kt
 * Path:          v3\src\main\kotlin\thirdparty\spotify\RotatingKeys.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-15 19:25:20 +02:00
 *
 * SHA-256: 9aa4722c98e066b58eabf9fefe122f1e4f1b2eb717d218b662ffde3d38f04bec
 *
 */


package thirdparty.spotify

import org.slf4j.LoggerFactory
import player.Player.spotifySourceManager
import java.io.*

object RotatingKeys {

    val keys: MutableList<SpotifyKey>
    private var currentKey: SpotifyKey
    private var currentSearchKey: SpotifyKey
    private val logger = LoggerFactory.getLogger(RotatingKeys::class.java)

    init {
        val file = javaClass
            .classLoader
            .getResourceAsStream("keys/spotify.txt") ?: throw IllegalArgumentException("Spotify keys file is not found")

        val rawKeysA = fileContent(file).split(";").filter { it.isNotBlank() }
        keys = rawKeysA.map { it.split(":") }.map { SpotifyKey(it[0], it[1]) }.toMutableList()

        currentKey = keys.random()
        currentSearchKey = keys.filter { it != currentKey }.random()
    }

    fun load() = Unit

    fun get(isSearch: Boolean = false): SpotifyKey = rotate(isSearch)
    fun getWithoutRotate(isSearch: Boolean = false): SpotifyKey = if (isSearch) currentSearchKey else currentKey


    fun rotate(isSearch: Boolean): SpotifyKey {
        return if (isSearch) {
            currentSearchKey = keys.filter { it != currentKey }.random()
            logger.info("Rotated search key to ${currentSearchKey.clientId}")
            SpotifyApi.requestToken(currentSearchKey)
            currentSearchKey
        } else {
            currentKey = keys.filter { it != currentSearchKey }.random()
            logger.info("Rotated global key to ${currentKey.clientId}")
            spotifySourceManager.requestToken(currentKey)
            currentKey
        }
    }

    fun rotateAll() {
        rotate(false)
        rotate(true)
    }

    fun removeKey(clientId: String): Boolean {
        val key = keys.find { it.clientId == clientId }
        if (key != null) {
            keys.remove(key)
            return true
        }
        return false
    }

    fun addKey(key: SpotifyKey) = keys.add(key)

    @Throws(IOException::class)
    private fun fileContent(`is`: InputStream): String {
        var lines = ""
        InputStreamReader(`is`).use { isr ->
            BufferedReader(isr).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    lines += line
                }
                `is`.close()
            }
        }
        return lines
    }
}