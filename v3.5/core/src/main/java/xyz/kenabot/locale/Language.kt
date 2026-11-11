/*
 * File:          Language.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\locale\Language.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 6d7a5ab65a226c40f7f82af8cb456aa8f4b0e0799cd5843cc43c733e2d55adf6
 *
 */


package xyz.kenabot.locale

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val hello: String
)