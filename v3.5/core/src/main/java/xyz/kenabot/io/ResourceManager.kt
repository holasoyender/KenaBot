/*
 * File:          ResourceManager.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\io\ResourceManager.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 8458c3b05df5da05bb9bf95c9603c638da231c304a1a21c550e20d2614ffada5
 *
 */


package xyz.kenabot.io

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class ResourceManager(
    private val path: String
) {

    val content: String
        get() {
            val file = getFileAsIOStream(path)
            return fileContent(file)
        }


    private fun getFileAsIOStream(fileName: String): InputStream {
        return javaClass
            .classLoader
            .getResourceAsStream(fileName) ?: throw IllegalArgumentException("$fileName is not found")
    }

    @Throws(IOException::class)
    private fun fileContent(`is`: InputStream): String {
        var lines = ""
        InputStreamReader(`is`).use { isr ->
            BufferedReader(isr).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    lines += line
                }
                `is`.close()
            }
        }
        return lines
    }

}