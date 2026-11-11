/*
 * File:          SpotifyKey.kt
 * Path:          v3\src\main\kotlin\thirdparty\spotify\SpotifyKey.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-04 00:03:12 +01:00
 *
 * SHA-256: 83586f6326dac3ba28da042c45a6e42d837cf62f15285bca54cdba4333d473b6
 *
 */


package thirdparty.spotify

class SpotifyKey(
    clientId: String,
    clientSecret: String
) {

    val clientId: String
    val clientSecret: String

    init {
        this.clientId = clientId
        this.clientSecret = clientSecret
    }

    override fun toString(): String {
        return "SpotifyKey(clientId='$clientId', clientSecret='$clientSecret')"
    }
}