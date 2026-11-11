/*
 * File:          Discord.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\Discord.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 0d672956f55fd8afe232ec3676e9a3e4d2e25302def4706ddfea8a595d8ee6a8
 *
 */


package xyz.kenabot

import dev.minn.jda.ktx.jdabuilder.injectKTX
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.Compression
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.dv8tion.jda.api.utils.messages.MessageRequest
import xyz.kenabot.config.Env
import xyz.kenabot.events.EventManager
import java.util.*
import kotlin.system.exitProcess

object Discord {

    fun loadShardingManager(): ShardManager {

        val builder = DefaultShardManagerBuilder.createDefault(null)

        MessageRequest.setDefaultMentionRepliedUser(false)

        builder.disableCache(EnumSet.allOf(CacheFlag::class.java))
        builder.enableCache(CacheFlag.VOICE_STATE)

        builder.setCompression(Compression.ZLIB)
        builder.setActivity(Activity.customStatus("/help - kenabot.xyz"))
        builder.setToken(Env.TOKEN)
        builder.setEventPassthrough(true)
        builder.setMemberCachePolicy(MemberCachePolicy.VOICE/*.or(MemberCachePolicy.NONE)*/)
        builder.setChunkingFilter(ChunkingFilter.NONE)
        builder.setLargeThreshold(50)
        builder.setShardsTotal(-1)

        builder.disableIntents(EnumSet.allOf(GatewayIntent::class.java))
        builder.enableIntents(
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.DIRECT_MESSAGES
        )

        builder.injectKTX()

        return try {
            builder.build().also {
                EventManager(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            exitProcess(1)
        }
    }
}