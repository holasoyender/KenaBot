/*
 * File:          Voice.kt
 * Path:          v3\src\main\kotlin\events\Voice.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-17 17:27:59 +02:00
 *
 * SHA-256: 114a644a20a748ed31e3bfeeff61e7ea75bf37d16bd5514bd666ad7a0836c79c
 *
 */


package events

import database.schema.Guild
import database.schema.Joins
import database.schema.Session
import langs.LangUtils
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel
import net.dv8tion.jda.api.events.guild.voice.*
import net.dv8tion.jda.api.hooks.ListenerAdapter
import player.Player
import utils.Constants

class Voice: ListenerAdapter() {

    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {

        val config = Guild.get(event.guild.id)
        val lang = LangUtils(config.language)

        if (event.channelLeft == null) {
            if (event.channelJoined != null) {
                // GuildVoiceJoinEvent
                if (event.member.user.id == event.jda.selfUser.id) {
                    if (event.channelJoined!!.type == ChannelType.STAGE) {

                        (event.channelJoined as StageChannel).requestToSpeak().queue()

                        val manager = Player.getMusicManager(event.guild.id) ?: return
                        val player = manager.player

                        if (event.channelJoined!!.members.size == 1 && player.isPaused)
                            player.isPaused = false

                    }

                    Joins.get(event.channelJoined!!.id).increment()
                } else {
                    if (event.channelJoined!!.members.size == 2 && event.guild.selfMember.voiceState?.channel?.id  == event.channelJoined!!.id) {

                        val manager = Player.getMusicManager(event.guild.id) ?: return
                        val player = manager.player

                        if (player.isPaused)
                            player.isPaused = false
                    }
                }
            }
        } else {
            if (event.channelJoined == null) {
                // GuildVoiceLeaveEvent

                if (event.member.user.id == event.jda.selfUser.id) {

                    val manager = Player.getMusicManager(event.guild.id) ?: return
                    val scheduler = manager.scheduler

                    scheduler.queue.clear()
                    scheduler.skip(null)

                    val (isHost, session) = Session.get(event.guild.id, event.guild.jda.selfUser.name)
                    if(session != null) {

                        if(isHost) {
                            session.listeners.forEach { listener ->
                                val guildManager = Player.getMusicManager(listener.guildId, true)!!
                                val guild = event.jda.shardManager!!.getGuildById(listener.guildId) ?: return@forEach

                                guildManager.scheduler.queue.clear()
                                guildManager.scheduler.skip(null, true)

                                val channel = guild.getTextChannelById(listener.channelId)
                                channel?.sendMessage(Constants.formatEmoji(Constants.Emojis.gear) + lang.get("commands.session.session_end").replace("%s%", session.inviteCode))
                                    ?.queue({}, {})
                            }

                            manager.channel?.sendMessage(Constants.formatEmoji(Constants.Emojis.gear) + lang.get("commands.session.session_end").replace("%s%", session.inviteCode))?.queue({}, {})
                            session.delete()
                        } else {
                            val listener = session.listeners.firstOrNull { it.guildId == event.guild.id && it.botName == event.guild.jda.selfUser.name }
                            if(listener != null) {
                                val guild = event.jda.shardManager!!.getGuildById(listener.guildId)
                                if(guild != null) {
                                    val channel = guild.getTextChannelById(listener.channelId)
                                    channel?.sendMessage(Constants.formatEmoji(Constants.Emojis.gear) + lang.get("commands.session.session_leave").replace("%s%", session.inviteCode))?.queue({}, {})
                                    session.listeners = session.listeners.filter { it.guildId != listener.guildId }
                                    session.save()
                                }
                            }
                        }

                    }

                } else {

                    if (event.channelLeft!!.members.size == 1 && event.channelLeft!!.members[0].user.id == event.jda.selfUser.id) {
                        if (!config.premiumEnabled || !config.radioEnabled) {

                            val manager = Player.getMusicManager(event.guild.id) ?: return
                            val scheduler = manager.scheduler

                            scheduler.queue.clear()
                            scheduler.skip(null, true)

                            val (isHost, session) = Session.get(event.guild.id, event.guild.jda.selfUser.name)
                            if(session != null) {

                                if (isHost) {
                                    session.listeners.forEach { listener ->
                                        val guildManager = Player.getMusicManager(listener.guildId, true)!!
                                        val guild =
                                            event.jda.shardManager!!.getGuildById(listener.guildId) ?: return@forEach

                                        guildManager.scheduler.queue.clear()
                                        guildManager.scheduler.skip(null, true)

                                        val channel = guild.getTextChannelById(listener.channelId)
                                        channel?.sendMessage(Constants.formatEmoji(Constants.Emojis.gear) + "La sesión `${session.inviteCode}` ha terminado!")
                                            ?.queue({}, {})
                                    }

                                    manager.channel?.sendMessage(Constants.formatEmoji(Constants.Emojis.gear) + "La sesión `${session.inviteCode}` ha terminado!")
                                        ?.queue({}, {})
                                    session.delete()
                                } else {
                                    val listener =
                                        session.listeners.firstOrNull { it.guildId == event.guild.id && it.botName == event.guild.jda.selfUser.name }
                                    if (listener != null) {
                                        val guild = event.jda.shardManager!!.getGuildById(listener.guildId)
                                        if (guild != null) {
                                            val channel = guild.getTextChannelById(listener.channelId)
                                            channel?.sendMessage(Constants.formatEmoji(Constants.Emojis.gear) + "He abandonado la sesión `${session.inviteCode}`")
                                                ?.queue({}, {})
                                            session.listeners =
                                                session.listeners.filter { it.guildId != listener.guildId }
                                            session.save()
                                        }
                                    }
                                }
                            }

                        } else {
                            if (config.radioChannelID.isNotBlank()) {

                                val manager = Player.getMusicManager(event.guild.id) ?: return
                                val player = manager.player

                                player.isPaused = true
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onGuildVoiceGuildMute(event: GuildVoiceGuildMuteEvent) {
        if (event.member.user.id == event.jda.selfUser.id) {

            if (event.member.voiceState!!.isGuildMuted || event.member.voiceState!!.isMuted) {
                val manager = Player.getMusicManager(event.guild.id) ?: return

                val player = manager.player
                player.isPaused = true
            }
            if (!event.member.voiceState!!.isGuildMuted || !event.member.voiceState!!.isMuted) {
                val manager = Player.getMusicManager(event.guild.id) ?: return

                val player = manager.player
                player.isPaused = false
            }
        }
    }
}