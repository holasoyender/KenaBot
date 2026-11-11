/*
 * File:          States.kt
 * Path:          v3\src\main\kotlin\enums\States.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 7982f9ce17fdf75da624de32f0dc766480df0581e9488ffc74530e61e7f065f9
 *
 */


package enums

enum class States {
    READY,
    SHARD_READY,
    SHARD_CONNECTED,
    SHARD_DISCONNECTED,
    UNKNOWN
}