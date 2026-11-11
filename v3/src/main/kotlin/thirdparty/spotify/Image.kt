/*
 * File:          Image.kt
 * Path:          v3\src\main\kotlin\thirdparty\spotify\Image.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-02-24 12:23:10 +01:00
 *
 * SHA-256: ee1a84e8f12bdd51186ff3ab0a0692109b7cf758ebaea2be4b352b9ebad41918
 *
 */


package thirdparty.spotify

import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser

class Image(
    json: JsonBrowser
) {

    val height: Int
    val width: Int
    val url: String

    init {
        this.height = json["height"].`as`(Int::class.java)
        this.width = json["width"].`as`(Int::class.java)
        this.url = json["url"].text()
    }

    override fun toString(): String {
        return "Image(height=$height, width=$width, url='$url')"
    }
}