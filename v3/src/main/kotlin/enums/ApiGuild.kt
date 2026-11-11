/*
 * File:          ApiGuild.kt
 * Path:          v3\src\main\kotlin\enums\ApiGuild.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 19b4eb16a410062d7eb928155a7a4fba5e27ea5904cf94b5426fc28825234e0c
 *
 */


package enums

data class ApiGuild(
    val id: String,
    val name: String,
    val icon: String,
    val owner: Boolean,
    val permissions: Int,
)