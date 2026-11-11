/*
 * File:          SpotifyApi.kt
 * Path:          v3\src\main\kotlin\thirdparty\spotify\SpotifyApi.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-15 19:25:20 +02:00
 *
 * SHA-256: 9166dec910dfcc7e6fdfe5858465f313796afe7de9b717fc0a7dbfd346bb7371
 *
 */


package thirdparty.spotify

import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import config.Env
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.message.BasicNameValuePair
import player.Player
import player.spotify.SpotifyAudioTrack
import player.spotify.SpotifySourceManager
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*
import java.util.stream.Collectors

object SpotifyApi {

    private val httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager()
    private var token: String? = null
    private var tokenExpire: Instant? = null

    fun requestToken(key: SpotifyKey) {

        val request = HttpPost("https://accounts.spotify.com/api/token")
        request.addHeader(
            "Authorization", "Basic " + Base64.getEncoder().encodeToString(
                (key.clientId + ":" + key.clientSecret).toByteArray(
                    StandardCharsets.UTF_8
                )
            )
        )
        request.entity = UrlEncodedFormEntity(
            listOf(BasicNameValuePair("grant_type", "client_credentials")),
            StandardCharsets.UTF_8
        )
        val json = HttpClientTools.fetchResponseAsJson(this.httpInterfaceManager.getInterface(), request)
        this.token = json["access_token"].text()
        this.tokenExpire = Instant.now().plusSeconds(json["expires_in"].asLong(0))
    }

    private fun getToken(): String {
        if (this.token == null || this.tokenExpire == null || this.tokenExpire!!.isBefore(Instant.now())) {
            RotatingKeys.rotate(true)
        }
        return this.token!!
    }

    private fun getJson(uri: String?): JsonBrowser? {
        val request = HttpGet(uri)
        request.addHeader("Authorization", "Bearer " + getToken())
        return try { HttpClientTools.fetchResponseAsJson(this.httpInterfaceManager.getInterface(), request) } catch (e: Exception) { null }
        /*return try {
            HttpClientTools.fetchResponseAsJson(this.httpInterfaceManager.getInterface(), request)
        } catch (e: Exception) {
            if (e is IOException && e.message?.contains("429") == true) {
                println("Spotify API rate limit exceeded, rotating search keys")
                RotatingKeys.rotate(true)
            } else {
                if (e is IOException) {
                    println("Spotify API error: ${e.message}")
                }
            }
            null
        }*/
    }

    private fun formatBio(artist: Artist, rawBio: String): String {
        var bio = rawBio
            .replace(Regex("<[^>]*>"), "")
            .replace(Regex("Last.fm"), "Spotify")
            .replace(Regex("last.fm"), "Spotify")
            .replace(Regex(artist.name), "**${artist.name}**")
            .replace("User-contributed text is available under the Creative Commons By-SA License; additional terms may apply.", "")

        if(bio.contains("\n\n1) "))
            bio = bio.substringAfter("\n\n1) ")

        val firstParagraph = bio.substringBefore("\n")

        return "$firstParagraph\n\n[Read more here](${artist.uri})"
    }

    fun getBiography(artist: Artist): String? {
        try {
            val request = HttpGet(
                "https://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=${
                    URLEncoder.encode(
                        artist.name,
                        StandardCharsets.UTF_8
                    )
                }&api_key=${Env.LASTFM_API_KEY}&format=json"
            )
            val obj = HttpClientTools.fetchResponseAsJson(this.httpInterfaceManager.getInterface(), request)
            val bio = obj["artist"]["bio"]["content"].text()
            if (bio.isBlank()) return null
            return formatBio(artist, bio)
        } catch (e: Exception) {
            return null
        }
    }

    fun searchForNames(query: String): Array<String> {
        val json = getJson(
            SpotifySourceManager.API_BASE + "search?q=" + URLEncoder.encode(
                query,
                StandardCharsets.UTF_8
            ) + "&type=track&limit=10"
        )
        return if (json == null || json["tracks"]["items"].values().isEmpty()) {
            arrayOf()
        } else {
            return json["tracks"]["items"].values().mapNotNull {

                if (it["is_local"].asBoolean(false)) {
                    null
                } else {
                    it["name"].text() + " - " + it["artists"].index(0)["name"].text()
                }
            }.toTypedArray()
        }
    }

    fun searchForArtist(query: String): Artist? {
        val json = getJson(
            SpotifySourceManager.API_BASE + "search?q=" + URLEncoder.encode(
                query,
                StandardCharsets.UTF_8
            ) + "&type=artist&limit=1"
        )
        return if (json == null || json["artists"]["items"].values().isEmpty()) {
            null
        } else {
            Artist(json)
        }
    }

    fun getArtistAlbums(artistId: String): Array<Album>? {
        val json = getJson(
            SpotifySourceManager.API_BASE + "artists/$artistId/albums?include_groups=album,single&limit=5"
        )
        return if (json == null || json["items"].values().isEmpty()) {
            null
        } else {
            json["items"].values().map { Album(it) }.toTypedArray()
        }
    }

    fun getRecommendations(track: AudioTrack, artist: Artist): List<AudioTrack> {
        val json = getJson(
            SpotifySourceManager.API_BASE + "recommendations?limit=10&seed_artists=${artist.id}&seed_tracks=${track.info.identifier}&seed_genres=${
                URLEncoder.encode(
                    artist.genres.take(3).joinToString(","),
                    StandardCharsets.UTF_8
                )
            }"
        )

        return if (json == null || json["tracks"].values().isEmpty()) {
            listOf()
        } else {
            json["tracks"].values().mapNotNull {
                try {
                    parseSpotifyTrack(it)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    private fun parseSpotifyTrack(json: JsonBrowser): AudioTrack {
        return SpotifyAudioTrack(
            AudioTrackInfo(
                json["name"].text(),
                json["artists"].values().stream().map { artist: JsonBrowser ->
                    artist["name"].text()
                }.collect(Collectors.joining(", ")),
                json["duration_ms"].asLong(0),
                json["id"].text(),
                false,
                json["external_urls"]["spotify"].text()
            ),
            json["external_ids"]["isrc"].text(),
            json["album"]["images"].index(0)["url"].text(),
            json["explicit"].asBoolean(false),
            Player.spotifySourceManager
        )
    }

}