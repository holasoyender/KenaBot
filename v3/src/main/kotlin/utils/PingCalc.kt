/*
 * File:          PingCalc.kt
 * Path:          v3\src\main\kotlin\utils\PingCalc.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-02-07 23:06:01 +01:00
 *
 * SHA-256: ee1f6ab4f7fd14d3d7d189a702dc9d6761fdf4ccb22894a6874c2e453d4f983c
 *
 */


package utils

object PingCalc {

    private var firstPing: Long = 0

    fun start() {
        firstPing = System.currentTimeMillis()
    }

    fun end(): Long {
        return System.currentTimeMillis() - firstPing
    }

}