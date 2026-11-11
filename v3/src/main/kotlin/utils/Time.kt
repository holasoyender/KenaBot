/*
 * File:          Time.kt
 * Path:          v3\src\main\kotlin\utils\Time.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-12-26 22:13:05 +01:00
 *
 * SHA-256: b475c00936d4122602f257dca43814cf063add10da0423b15a2fa7781b12e8c1
 *
 */


package utils

class Time(time: String) {
    init {
        Companion.time
        ms(time)
    }

    companion object {
        val time: Long
            get() = System.currentTimeMillis()

        fun ms(_time: String): Long {
            var time = _time
            if (time.endsWith("s")) {
                time = time.replace("s", "")
                return try {
                    time.toLong() * 1000
                } catch (e: Exception) {
                    0
                }
            }
            if (time.endsWith("m")) {
                time = time.replace("m", "")
                return try {
                    time.toLong() * 60000
                } catch (e: Exception) {
                    0
                }
            }
            if (time.endsWith("h")) {
                time = time.replace("h", "")
                return try {
                    time.toLong() * 3600000
                } catch (e: Exception) {
                    0
                }
            }
            if (time.endsWith("d")) {
                time = time.replace("d", "")
                return try {
                    time.toLong() * 86400000
                } catch (e: Exception) {
                    0
                }
            }
            return try {
                time.toLong()
            } catch (e: Exception) {
                0
            }
        }
    }
}