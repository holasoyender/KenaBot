/*
 * File:          Autocomplete.kt
 * Path:          v3\src\main\kotlin\thirdparty\spotify\Autocomplete.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-02-24 12:23:10 +01:00
 *
 * SHA-256: cc0a8c45ca8368182f66d037721d6ff8b6b8dddf87996923d003b8b5524469f0
 *
 */


package thirdparty.spotify

object Autocomplete {
    fun search(query: String): List<String> {
        val results = mutableListOf<String>()
        val spotifyResults = SpotifyApi.searchForNames(query)
        results.addAll(spotifyResults)
        return results
    }
}