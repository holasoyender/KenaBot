/*
 * File:          SongSearch.kt
 * Path:          v3\src\main\kotlin\thirdparty\genius\genius\SongSearch.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 73efdaa9473b9e6307d470db49f9c0e07544d3d43c73691983649a69818e4952
 *
 */


package thirdparty.genius.genius

import config.Env
import io.sentry.Sentry
import org.json.JSONObject
import thirdparty.genius.core.GLA
import java.io.IOException
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URLEncoder
import java.util.*

class SongSearch/* As suggested by User DimitrisStaratzis, for consistent encoding on all devices*/(
    val gla: GLA,
    _query: String
) {
    var status = 0
        private set
    var nextPage = 0
        private set
    val hits = LinkedList<Hit>()

    init {
        var query = _query
        query = URLEncoder.encode(
            query,
            "UTF-8" /* As suggested by User DimitrisStaratzis, for consistent encoding on all devices*/
        )
        try {
            val uri = URI("https://api.genius.com/search?q=$query")
            requestWithAuth(uri)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            Sentry.captureException(e)
        }
    }

    @Throws(IOException::class)
    private fun requestWithAuth(uri: URI) {
        try {
            val con = gla.httpManager.getConnection(uri.toURL())
            con.setRequestProperty("Authorization", "Bearer "+Env.GENIUS_API)

            val result = gla.httpManager.executeGet(con)
            parseAuth(JSONObject(result))
        } catch (e: MalformedURLException) {
            throw InternalError()
        }
    }

    private fun parseAuth(jRoot: JSONObject) {
        status = jRoot.getJSONObject("meta").getInt("status")
        val response = jRoot.getJSONObject("response")
        if (!response.isNull("next_page")) {
            nextPage = response.getInt("next_page")
        }
        val hits = response.getJSONArray("hits")
        for (i in 0 until hits.length()) {
            val hitRoot = hits.getJSONObject(i).getJSONObject("result")
            this.hits.add(Hit(hitRoot))
        }
    }

    inner class Hit(jRoot: JSONObject) {
        val id: Long
        val title: String
        val titleWithFeatured: String
        val url: String
        val imageUrl: String
        val thumbnailUrl: String
        val artist: Artist

        init {
            id = jRoot.getLong("id")
            title = jRoot.getString("title")
            titleWithFeatured = jRoot.getString("title_with_featured")
            url = jRoot.getString("url")
            imageUrl = jRoot.getString("header_image_url")
            thumbnailUrl = jRoot.getString("song_art_image_thumbnail_url")
            artist = Artist(jRoot.getJSONObject("primary_artist"))
        }

        @Suppress("unused")
        fun fetchLyrics(): String {
            return LyricsParser()[id.toString() + ""]!!
        }
    }

    inner class Artist(jRoot: JSONObject) {
        val id: Long
        val imageUrl: String
        val name: String
        //val slug: String
        val url: String

        init {
            id = jRoot.getLong("id")
            imageUrl = jRoot.getString("image_url")
            name = jRoot.getString("name")
            //slug = jRoot.getString("slug")
            url = jRoot.getString("url")
        }
    }
}