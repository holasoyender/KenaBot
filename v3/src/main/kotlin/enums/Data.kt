/*
 * File:          Data.kt
 * Path:          v3\src\main\kotlin\enums\Data.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-06-29 21:43:39 +02:00
 *
 * SHA-256: 3ab18554d09e2e99d6bd6b2ae6ea3c45d9b62ac272cc9c244f1aa10b5e2d5dca
 *
 */


package enums

data class Data(
    val user: ApiUser?,
    val params: List<String>,
    val body: String?
)