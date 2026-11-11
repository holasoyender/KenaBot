/*
 * File:          PlayerTimestamp.kt
 * Path:          v3\src\main\kotlin\utils\PlayerTimestamp.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-12-26 22:28:33 +01:00
 *
 * SHA-256: aa633f11a1bdf21278f236e634f24982ea9aedd1c3cf2d2af3ee45f2d16ee4e4
 *
 */


package utils

object PlayerTimestamp {
    fun get(milliseconds: Long): String {

        val seconds = (milliseconds / 1000).toInt() % 60
        val minutes = (milliseconds / (1000 * 60) % 60).toInt()
        val hours = (milliseconds / (1000 * 60 * 60) % 24).toInt()
        return if (hours > 0) String.format(
            "%02d:%02d:%02d",
            hours,
            minutes,
            seconds
        ) else String.format("%02d:%02d", minutes, seconds)

    }
}