/*
 * File:          PlayButtons.kt
 * Path:          v3\src\main\kotlin\handlers\PlayButtons.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-13 22:46:09 +02:00
 *
 * SHA-256: 23526b45c0c01e24345d8c50539adfbc20c8b48bc596ff735806def7e12c2685
 *
 */


package handlers

import database.schema.Guild
import database.schema.Session
import enums.LoopMode
import functions.DjRole
import functions.StringFormat
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.exceptions.PermissionException
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import player.Player
import utils.*
import utils.Constants.formatEmoji

object PlayButtons {

    fun handleLoop(event: ButtonInteraction) {

        val config = Guild.get(event.guild!!.id)
        val lang = LangUtils(config.language)

        if (event.member!!.voiceState?.inAudioChannel() == false) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue")).setEphemeral(true)
                .queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.channel == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true)
                .queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.inAudioChannel() == true) {
            if (event.guild!!.selfMember.voiceState!!.channel != event.member!!.voiceState!!.channel) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue"))
                    .setEphemeral(true).queue()
                return
            }
        } else {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true)
                .queue()
            return
        }

        val manager = Player.getMusicManager(event.guild!!.id)

        if (manager == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true)
                .queue()
            return
        }

        val player = manager.player
        val scheduler = manager.scheduler

        if (DjRole.checkChannel(event.guild!!.selfMember.voiceState?.channel, config, scheduler)) {
            if (!DjRole.checkRole(event.member!!, config, scheduler)) {
                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setDescription(lang.get("manager.dj_role_required"))
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                return
            }
        }

        if (player.playingTrack == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true)
                .queue()
            return
        }

        val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
        if (session != null) {
            if (!isHost) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.session.cant_modify_session"))
                    .setEphemeral(true).queue()
                return
            }

            session.listeners.forEach { listener ->
                val guildManager = Player.getMusicManager(listener.guildId, true)!!
                guildManager.scheduler.isRepeating = when (scheduler.isRepeating) {
                    LoopMode.NONE -> LoopMode.TRACK
                    LoopMode.TRACK -> LoopMode.QUEUE
                    LoopMode.QUEUE -> LoopMode.NONE
                }
            }
        }

        scheduler.isRepeating = when (scheduler.isRepeating) {
            LoopMode.NONE -> LoopMode.TRACK
            LoopMode.TRACK -> LoopMode.QUEUE
            LoopMode.QUEUE -> LoopMode.NONE
        }

//        val embed = EmbedBuilder()
//            .setColor(config.embedColor)
//            .setDescription(
//                formatEmoji(Constants.Emojis.success) + lang.get("commands.loop.action").replace(
//                    "%s%",
//                    if (scheduler.isRepeating == LoopMode.TRACK) lang.get("commands.loop.repeat") else lang.get("commands.loop.no_repeat")
//                )
//            )

        val state = when (scheduler.isRepeating) {
            LoopMode.NONE -> lang.get("commands.loop.no_repeat")
            LoopMode.TRACK -> lang.get("commands.loop.repeat_track")
            LoopMode.QUEUE -> lang.get("commands.loop.repeat_queue")
        }

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setDescription(lang.get("commands.loop.info").replace("%s%", state))

//        event.message.editMessageComponents(
//            ActionRow.of(
//                if (scheduler.isRepeating == LoopMode.TRACK)
//                    Button.primary(
//                        "cmd:play:loop",
//                        lang.get("commands.loop.repeat").replaceFirstChar { it.uppercase() })
//                else
//                    Button.secondary(
//                        "cmd:play:loop",
//                        lang.get("commands.loop.no_repeat").replaceFirstChar { it.uppercase() })
//            )
//        ).setEmbeds(embed.build()).queue()

        event.editMessageEmbeds(embed.build()).setActionRow(
            when (scheduler.isRepeating) {
                LoopMode.TRACK -> Button.primary(
                    "cmd:play:loop",
                    lang.get("commands.loop.repeat_track").replaceFirstChar { it.uppercase() })
                LoopMode.QUEUE -> Button.success(
                    "cmd:play:loop",
                    lang.get("commands.loop.repeat_queue").replaceFirstChar { it.uppercase() })
                LoopMode.NONE -> Button.secondary(
                    "cmd:play:loop",
                    lang.get("commands.loop.no_repeat").replaceFirstChar { it.uppercase() })
            }
        ).queue()
    }

    fun handlePlay(event: ButtonInteraction) {

        val config = Guild.get(event.guild!!.id)
        val lang = LangUtils(config.language)
        val url = event.componentId.split(":")[3] + ":" + event.componentId.split(":")[4]

        if (event.member!!.voiceState?.inAudioChannel() == false) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_vc")).setEphemeral(true).queue()
            return
        }

        val channel = event.member!!.voiceState?.channel
        val manager = Player.getMusicManager(event.guild!!, event.channel as GuildMessageChannel, true)!!

        if (channel == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_vc")).setEphemeral(true)
                .queue()
            return
        }

        if (!channel.guild.selfMember.hasPermission(
                channel,
                Permission.VOICE_CONNECT
            ) || !channel.guild.selfMember.hasPermission(channel, Permission.VOICE_SPEAK)
        ) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_vc_perms")).setEphemeral(true)
                .queue()
            return
        }

        if (config.disabledVoiceChannels.contains(channel.id)) {
            if (!event.member!!.hasPermission(Permission.MANAGE_SERVER)) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.vc_disabled"))
                    .setEphemeral(true).queue()
                return
            }
        }

        if (channel.type == ChannelType.STAGE && !config.premiumEnabled) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.no_premium_stage")).setEphemeral(true)
                .queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.channel !== null) {
            if (event.guild!!.selfMember.voiceState!!.channel !== channel) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.different_vc"))
                    .setEphemeral(true).queue()
                return
            }
        } else {
            try {
                manager.link.connect(channel)
            } catch (e: PermissionException) {
                if (e.permission === Permission.VOICE_CONNECT) {
                    event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_vc_perms"))
                        .setEphemeral(true).queue()
                    return
                }
            }
        }

        manager.channel = event.channel as GuildMessageChannel
        Player.loadAndPlay(manager, event, url, false, config, lang)
    }

    fun handlePause(event: ButtonInteraction) {

        val config = Guild.get(event.guild!!.id)
        val lang = LangUtils(config.language)

        if (event.member!!.voiceState?.inAudioChannel() == false) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue")).setEphemeral(true).queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.channel == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.inAudioChannel() == true) {
            if (event.guild!!.selfMember.voiceState!!.channel != event.member!!.voiceState!!.channel) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue")).setEphemeral(true).queue()
                return
            }
        } else {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val manager = Player.getMusicManager(event.guild!!.id)

        if (manager == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val player = manager.player
        val scheduler = manager.scheduler

        if (DjRole.checkChannel(event.guild!!.selfMember.voiceState?.channel, config, scheduler)) {
            if (!DjRole.checkRole(event.member!!, config, scheduler)) {
                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setDescription(lang.get("manager.dj_role_required"))
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                return
            }
        }

        if (player.playingTrack == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
        if(session != null) {
            if(!isHost) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.session.cant_modify_session")).setEphemeral(true).queue()
                return
            }

            session.listeners.forEach { listener ->
                val guildManager = Player.getMusicManager(listener.guildId, true)!!
                guildManager.player.isPaused = !player.isPaused
            }
        }

        if (player.isPaused) {
            player.isPaused = false

            val embed = EmbedBuilder()
                .setColor(config.embedColor)
                .setDescription(lang.get("commands.pause.resumed"))

            event.replyEmbeds(embed.build()).setEphemeral(true).queue()
        } else {
            player.isPaused = true

            val embed = EmbedBuilder()
                .setColor(config.embedColor)
                .setDescription(lang.get("commands.pause.paused"))

            event.replyEmbeds(embed.build()).setEphemeral(true).queue()
        }
    }

    fun handleSkip(event: ButtonInteraction) {

        val config = Guild.get(event.guild!!.id)
        val lang = LangUtils(config.language)

        if (event.member!!.voiceState?.inAudioChannel() == false) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue")).setEphemeral(true).queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.channel == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.inAudioChannel() == true) {
            if (event.guild!!.selfMember.voiceState!!.channel != event.member!!.voiceState!!.channel) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue")).setEphemeral(true).queue()
                return
            }
        } else {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val manager = Player.getMusicManager(event.guild!!.id)

        if (manager == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val scheduler = manager.scheduler

        if (DjRole.checkChannel(event.guild!!.selfMember.voiceState?.channel, config, scheduler)) {
            if (!DjRole.checkRole(event.member!!, config, scheduler)) {
                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setDescription(lang.get("manager.dj_role_required_usecmd"))
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                return
            }
        }

        val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
        if(session != null) {
            if (!isHost) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.session.cant_modify_session"))
                    .setEphemeral(true).queue()
                return
            }

            session.listeners.forEach { listener ->
                val guildManager = Player.getMusicManager(listener.guildId, true)!!
                guildManager.scheduler.isRepeating = LoopMode.NONE
                guildManager.scheduler.skip(guildManager.scheduler.lastChannel, scheduler.queue.isEmpty())
            }
        }

        scheduler.isRepeating = LoopMode.NONE
        scheduler.skip(event.channel as GuildMessageChannel, scheduler.queue.isEmpty())

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setDescription(lang.get("commands.skip"))

        event.replyEmbeds(embed.build()).setEphemeral(true).queue()
    }

    fun handleStop(event: ButtonInteraction) {

        val config = Guild.get(event.guild!!.id)
        val lang = LangUtils(config.language)

        if (event.member!!.voiceState?.inAudioChannel() == false) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue")).setEphemeral(true).queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.channel == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.inAudioChannel() == true) {
            if (event.guild!!.selfMember.voiceState!!.channel != event.member!!.voiceState!!.channel) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue")).setEphemeral(true).queue()
                return
            }
        } else {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val manager = Player.getMusicManager(event.guild!!.id)

        if (manager == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }
        val player = manager.player
        val scheduler = manager.scheduler

        if (DjRole.checkChannel(event.guild!!.selfMember.voiceState?.channel, config, scheduler)) {
            if (!DjRole.checkRole(event.member!!, config, scheduler)) {
                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setDescription(lang.get("manager.dj_role_required"))
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                return
            }
        }

        if (player.playingTrack == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
        if(session != null) {
            if (!isHost) {
                val listener =
                    session.listeners.firstOrNull { it.guildId == event.guild!!.id && it.botName == event.guild!!.jda.selfUser.name }
                if (listener != null) {
                    val channel = event.guild!!.getTextChannelById(listener.channelId)
                    channel?.sendMessage(formatEmoji(Constants.Emojis.gear) + lang.get("commands.session.session_leave").replace("%s%", session.inviteCode))
                        ?.queue({}, {})
                    session.listeners = session.listeners.filter { it.guildId != listener.guildId }
                    session.save()
                }
            } else {

                session.listeners.forEach { listener ->
                    val guildManager = Player.getMusicManager(listener.guildId, true)!!
                    val guild = event.jda.shardManager!!.getGuildById(listener.guildId) ?: return@forEach

                    guildManager.scheduler.queue.clear()
                    guildManager.scheduler.isRepeating = LoopMode.NONE
                    guildManager.scheduler.skip(null, true)

                    val channel = guild.getTextChannelById(listener.channelId)
                    channel?.sendMessage(formatEmoji(Constants.Emojis.gear) + lang.get("commands.session.session_end").replace("%s%", session.inviteCode))
                        ?.queue({}, {})
                }

                event.channel.sendMessage(formatEmoji(Constants.Emojis.gear) + lang.get("commands.session.session_end").replace("%s%", session.inviteCode))
                    .queue({}, {})
                session.delete()
            }
        }

        scheduler.queue.clear()
        scheduler.isRepeating = LoopMode.NONE
        scheduler.skip(event.channel as GuildMessageChannel, true)

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setDescription(lang.get("commands.stop"))

        event.replyEmbeds(embed.build()).setEphemeral(true).queue()
    }

    fun handleQueue(event: ButtonInteraction) {
        val config = Guild.get(event.guild!!.id)
        val lang = LangUtils(config.language)

        if (event.guild!!.selfMember.voiceState?.channel == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val manager = Player.getMusicManager(event.guild!!.id)

        if (manager == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val player = manager.player

        if (player.playingTrack == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val queue = manager.scheduler.queue
        val chunk = queue.chunked(5)

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setFooter(
                lang.get("commands.queue.embed_footer").replace("%s%", "1").replace("%d%", chunk.size.toString())
                    .replace("%f%", (queue.size + 1).toString())
            )
            .setAuthor(lang.get("commands.queue.embed_author"), null, event.jda.selfUser.avatarUrl)

        if (manager.player.playingTrack != null) {
            embed.setDescription("```yml\n --- ${lang.get("commands.queue.now_playing")} ---\n${StringFormat.format(manager.player.playingTrack.info.title)}```")
        }

        if (chunk.isNotEmpty())
            for ((i, track) in chunk.first().withIndex()) {
                embed.addField(
                    "${i + 1}. ${StringFormat.format(track.info.title)}",
                    "**Autor:** ${track.info.author}\n**Duración**: ${PlayerTimestamp.get(track.duration)}",
                    false
                )
            }

        event.replyEmbeds(embed.build()).addActionRow(
            Button.primary("cmd:queue:0:back:" + event.user.id, "◀").asDisabled(),
            Button.primary("cmd:queue:0:next:" + event.user.id, "▶").withDisabled(chunk.size == 1)
        ).setEphemeral(true).queue()
    }
}