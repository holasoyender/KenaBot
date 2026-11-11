/*
 * File:          GLA.kt
 * Path:          v3\src\main\kotlin\thirdparty\genius\core\GLA.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-29 12:59:00 +02:00
 *
 * SHA-256: a610422f0ffee4a43aa5397dfada087a25cb424f5962e2be73869ca4171df5de
 *
 */


package thirdparty.genius.core

import thirdparty.genius.genius.SongSearch
import java.io.IOException

class GLA {
    val httpManager = HttpManager()

    @Throws(IOException::class)
    fun search(query: String?): SongSearch {
        return SongSearch(this, query!!)
    }
}