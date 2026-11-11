/*
 * File:          GuildCache.kt
 * Path:          v3\src\main\kotlin\restarter\GuildCache.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-12-28 17:50:28 +01:00
 *
 * SHA-256: 0781abeb8649677df268209c8792a2f5c1cabe19dd902405af1ce9c47c32ced6
 *
 */


package restarter

class GuildCache(
    val guildId: String,
    val shardId: Int,
    var done: Boolean,
) {
    override fun toString(): String {
        return "GuildCache(guildId='$guildId', shardId=$shardId, done=$done)"
    }

    companion object {

        private val cache = mutableListOf<GuildCache>()

        fun add(guildId: String, shardId: Int) {
            cache.add(GuildCache(guildId, shardId, false))
        }

        fun add(guildCache: GuildCache) {
            cache.add(guildCache)
        }

        fun remove(guildId: String) {
            cache.removeIf { it.guildId == guildId }
        }

        fun get(guildId: String): GuildCache? {
            return cache.firstOrNull { it.guildId == guildId }
        }

    }
}