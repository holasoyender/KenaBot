/*
 * File:          Progressbar.kt
 * Path:          v3\src\main\kotlin\functions\Progressbar.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 6095b7729cd5d67caefe91ce3a03ba1a55d6e0d9445958c49d41732b2563586a
 *
 */


package functions

import kotlin.math.roundToInt

fun progress(total: Int, current: Int, size: Int = 40, line: String, slider: String ): String {

    return if(current > total) {
        line.repeat(size+2)
    } else {
        val percentage = current.toDouble() / total
        val progress = (percentage * size).roundToInt()
        val emptyProgress = size - progress
        val progressText = line.repeat(progress)+ slider
        val emptyProgressText = line.repeat(emptyProgress)
        progressText + emptyProgressText
    }
}