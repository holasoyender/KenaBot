/*
 * File:          Check247.kt
 * Path:          v3\src\main\kotlin\services\Check247.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 9558fc43575f669ea56cbf48526a4cb42d8cd387a7aede5137cbf93e93de3a34
 *
 */


package services

import database.schema.Guild
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel
import net.dv8tion.jda.api.sharding.ShardManager
import kotlin.concurrent.thread

object Check247 {

    fun startLoop(shardManager: ShardManager) {

        thread(isDaemon = true, name = "Check247Service") {
            while (true) {
                val allGuilds = Guild.getAllPremium()
                allGuilds.forEach { guild ->
                    if (guild.radioEnabled) {
                        if (guild.radioChannelID.isNotBlank()) {

                            try {
                                val cacheGuild = shardManager.getGuildById(guild.guildID)
                                if (cacheGuild != null) {

                                    var channel: AudioChannel? =
                                        cacheGuild.getVoiceChannelById(guild.radioChannelID)
                                    if (channel == null)
                                        channel = cacheGuild.getStageChannelById(guild.radioChannelID)

                                    if (channel != null) {

                                        if (cacheGuild.selfMember.voiceState?.channel != null) {
                                            if (cacheGuild.selfMember.voiceState?.channel?.id != guild.radioChannelID && cacheGuild.audioManager.isConnected) {
                                                cacheGuild.audioManager.openAudioConnection(channel)
                                                cacheGuild.audioManager.isSelfDeafened = true
                                            }
                                        } else {
                                            cacheGuild.audioManager.openAudioConnection(channel)
                                            cacheGuild.audioManager.isSelfDeafened = true
                                        }

                                    }
                                }
                            } catch (_: Exception) {
                            }
                        }
                    }
                }
                Thread.sleep(30000)
            }
        }
    }
}