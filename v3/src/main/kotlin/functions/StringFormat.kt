/*
 * File:          StringFormat.kt
 * Path:          v3\src\main\kotlin\functions\StringFormat.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-02-21 22:47:55 +01:00
 *
 * SHA-256: 2efc02baccf4c72cc4337db12e670abfd8c238e85285775832c7e651dede8042
 *
 */


package functions

object StringFormat {
    fun format(str: String): String {
        return str
            .replace(Regex("\\(.*?\\)"), "")
            .replace(Regex("\\{.*?}"), "")
            .replace(Regex("\\[.*?]"), "")
            .replace(Regex("<.*?>"), "")
            .replace("lyric", "")
            .replace("lyrics", "")
            .replace("music", "")
            .replace("video", "")
            .replace(Regex("\\*"), "\\\\*")
    }
}