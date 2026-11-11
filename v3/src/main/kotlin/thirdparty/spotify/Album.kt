/*
 * File:          Album.kt
 * Path:          v3\src\main\kotlin\thirdparty\spotify\Album.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-03 15:52:10 +01:00
 *
 * SHA-256: 449a608cc1677ca03c4cd5fd2b1a912495538182129c022a88c17cb1ea1e6a77
 *
 */


package thirdparty.spotify

import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser

class Album(
    album: JsonBrowser
) {

    val uri: String
    val name: String
    val images: MutableList<Image>

    init {
        this.uri = album["external_urls"]["spotify"].text()
        this.name = album["name"].text()
        this.images = album["images"].values().map { m -> Image(m) }.toMutableList()
    }

    override fun toString(): String {
        return "Album(uri='$uri', name='$name', images=$images)"
    }
}