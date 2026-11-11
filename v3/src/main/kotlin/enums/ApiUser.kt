/*
 * File:          ApiUser.kt
 * Path:          v3\src\main\kotlin\enums\ApiUser.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: db8f8c4dfa15b45c82e493409c594771adb62d8888dc43be3a10d22d199f4a80
 *
 */


package enums

data class ApiUser(
    val id: String,
    val username: String,
    val avatar: String,
    val guilds: List<ApiGuild>
)