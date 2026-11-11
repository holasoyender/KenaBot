/*
 * File:          LyricsParser.kt
 * Path:          v3\src\main\kotlin\thirdparty\genius\genius\LyricsParser.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 6c8528814dd342f89e6179c3de396259d7c8b1b1deed012d09691b64fc3c2ad7
 *
 */


package thirdparty.genius.genius

import thirdparty.genius.core.Statics
import java.io.IOException
import java.net.URL
import java.util.*

class LyricsParser    //Construct crawlCache
{
    operator fun get(id: String): String? {
        return parseLyrics(id)
    }

    private fun parseLyrics(id: String): String? {
        return try {

            //val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(ProxyManager.proxy, 1022))

            val connection = URL(GENIUS_EMBED_URL_HEAD + id + GENIUS_EMBED_URL_TAIL).openConnection()
            connection.setRequestProperty("User-Agent", Statics.USER_AGENT)
            connection.setRequestProperty("accept-language", "en-US")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Connection", "keep-alive")
            connection.setRequestProperty("DNT", "1")
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1")
            connection.setRequestProperty("Accept-Encoding", "none")
            connection.setRequestProperty("Sec-Ch-Ua", "\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"105\", \"Chromium\";v=\"105\"")
            connection.setRequestProperty("Sec-Ch-Ua-Mobile", "?0")
            connection.setRequestProperty("Sec-Fetch-Dest", "document")
            connection.setRequestProperty("Sec-Fetch-Mode", "no-cors")
            connection.setRequestProperty("Sec-Fetch-Site", "cross-site")

            val scanner = Scanner(connection.getInputStream())
            scanner.useDelimiter("\\A")
            var raw = ""
            while (scanner.hasNext()) {
                raw += scanner.next()
            }
            if (raw == "") {
                null
            } else getReadable(raw)
        } catch (e: IOException) {
            e.printStackTrace()
            //Sentry.captureException(e)
            null
        }
    }

    private fun getReadable(lyrics: String): String {
        //Remove start
        var rawLyrics = lyrics
        rawLyrics = rawLyrics.replace(
            "[\\S\\s]*<div class=\\\\\\\\\\\\\"rg_embed_body\\\\\\\\\\\\\">[ (\\\\n)]*".toRegex(),
            ""
        )
        //Remove end
        rawLyrics = rawLyrics.replace("[ (\\\\n)]*<\\\\/div>[\\S\\s]*".toRegex(), "")
        //Remove tags between
        rawLyrics = rawLyrics.replace("<[^<>]*>".toRegex(), "")
        //Unescape spaces
        rawLyrics = rawLyrics.replace("\\\\\\\\n".toRegex(), "\n")
        //Unescape '
        rawLyrics = rawLyrics.replace("\\\\'".toRegex(), "'")
        //Unescape "
        rawLyrics = rawLyrics.replace("\\\\\\\\\\\\\"".toRegex(), "\"")
        return rawLyrics
    }

    companion object {
        private const val GENIUS_EMBED_URL_HEAD = "https://genius.com/songs/"
        private const val GENIUS_EMBED_URL_TAIL = "/embed.js"
    }
}