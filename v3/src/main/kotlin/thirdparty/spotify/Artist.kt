/*
 * File:          Artist.kt
 * Path:          v3\src\main\kotlin\thirdparty\spotify\Artist.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-05 16:49:35 +01:00
 *
 * SHA-256: 1d6986049b898f9339eb6a8793563000e60f7ccce4a2ebbe458a5df6c77cca4d
 *
 */


package thirdparty.spotify

import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser
import java.util.*

class Artist(
    json: JsonBrowser
) {

    val uri: String
    val followers: Int
    val genres: MutableList<String>
    val name: String
    val popularity: Int
    val bio: String?
    val images: MutableList<Image>
    val id: String

    init {
        val artist = (json["artists"]["items"].values().firstOrNull() ?: json["artists"].values().firstOrNull())!!
        this.uri = artist["external_urls"]["spotify"].text()
        this.followers = artist["followers"]["total"].`as`(Int::class.java)
        this.genres = artist["genres"].values().map { m -> m.text().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        } }.toMutableList()
        this.name = artist["name"].text()
        this.popularity = artist["popularity"].`as`(Int::class.java)
        this.images = artist["images"].values().map { m -> Image(m) }.toMutableList()
        this.id = artist["id"].text()
        this.bio = SpotifyApi.getBiography(this)
    }

    /*private fun getBiography(): String? {

        /*val caller = URL("https://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=${URLEncoder.encode(this.name, StandardCharsets.UTF_8)}&api_key=${Env.LASTFM_API_KEY}&format=json")
        val http = caller.openConnection() as HttpURLConnection
        http.requestMethod = "GET"
        http.setRequestProperty("Accept", "application/json")
        if (http.responseCode != 200) {
            return null
        }

        var inline = ""
        val scanner = Scanner(caller.openStream())
        while (scanner.hasNext()) {
            inline += scanner.nextLine()
        }
        scanner.close()
        http.disconnect()

        if(inline.isEmpty()) return null

        val obj = try { JsonBrowser.parse(inline) } catch (e: Exception) { return null }*/

        val bio = obj["artist"]["bio"]["content"].text()
        if (bio.isBlank()) return null
        return formatBio(bio)
    }

    private fun formatBio(rawBio: String): String {
        var bio = rawBio
            .replace(Regex("<[^>]*>"), "")
            .replace(Regex("Last.fm"), "Spotify")
            .replace(Regex("last.fm"), "Spotify")
            .replace(Regex(this.name), "**${this.name}**")

        if(bio.contains("\n\n1) "))
            bio = bio.substringAfter("\n\n1) ")

        val firstParagraph = bio.substringBefore("\n")

        return "$firstParagraph\n\nRead more: ${this.uri}"
    }*/

    override fun toString(): String {
        return "Artist(uri='$uri', followers=$followers, genres=$genres, name='$name', popularity=$popularity, images=$images)"
    }

    fun getLargestImage(): Image? {
        return images.maxByOrNull { it.height }
    }

    fun getSmallestImage(): Image? {
        return images.minByOrNull { it.height }
    }
}