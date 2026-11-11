/*
 * File:          Leave.kt
 * Path:          v3\src\main\kotlin\commands\Leave.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-18 19:41:50 +02:00
 *
 * SHA-256: a5c378ed172e9dffb006f84a78cc220ff4f1dae5a00024c2cf13a71d9fbaee0a
 *
 */


package commands

import database.schema.Guild
import database.schema.Session
import enums.LoopMode
import functions.DjRole
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import player.Player
import utils.Constants
import utils.Constants.formatEmoji

class Leave: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        if(config.premiumEnabled) {
            if(config.radioEnabled) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.is247")).setEphemeral(true).queue()
                return
            }
        }

        if (event.member!!.voiceState?.inAudioChannel() == false) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue")).setEphemeral(true).queue()
            return
        }

        if(event.guild!!.selfMember.voiceState?.channel == null) {
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

        val manager = Player.getMusicManager(event.guild!!) ?: return event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
        val scheduler = manager.scheduler

        if(DjRole.checkChannel(event.guild!!.selfMember.voiceState?.channel, config, scheduler)) {
            if(!DjRole.checkRole(event.member!!, config, scheduler)) {
                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setDescription(lang.get("manager.dj_role_required"))
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                return
            }
        }

        val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
        if(session != null) {
            if(!isHost) {
                event.reply(formatEmoji(Constants.Emojis.error) + "No puedes modificar la cola de reproducción mientras estás en una sesión!").setEphemeral(true).queue()
                return
            }

            session.listeners.forEach { listener ->

                val guildManager = Player.getMusicManager(listener.guildId, true)!!
                val guild = event.jda.shardManager!!.getGuildById(listener.guildId) ?: return@forEach

                guildManager.scheduler.queue.clear()
                guildManager.scheduler.isRepeating = LoopMode.NONE
                guildManager.scheduler.skip(null, true)

                val channel = guild.getTextChannelById(listener.channelId)
                channel?.sendMessage(formatEmoji(Constants.Emojis.gear) + "La sesión `${session.inviteCode}` ha terminado!")
                    ?.queue({}, {})

            }
            event.channel.sendMessage(formatEmoji(Constants.Emojis.gear) + "La sesión `${session.inviteCode}` ha terminado!").queue({}, {})
            session.delete()
        }

        scheduler.queue.clear()
        scheduler.isRepeating = LoopMode.NONE
        scheduler.skip(null, true)

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setDescription(lang.get("commands.stop_leave"))

        event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()

    }

    override val name: String
        get() = "leave"
    override val description: String
        get() = "Sacar del canal de voz al bot"
    override val category: String
        get() = "Music"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf()
    override val usage: String
        get() = ""
    override val premium: Boolean
        get() = false
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Sacar del canal de voz al bot",
                    DiscordLocale.ENGLISH_US to "Kick the bot from the voice channel",
                    DiscordLocale.ENGLISH_UK to "Kick the bot from the voice channel",
                    DiscordLocale.FRENCH to "Expulse le bot du canal vocal",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Expulse o bot do canal de voz",
                    DiscordLocale.ITALIAN to "Espelli il bot dal canale vocale",
                    DiscordLocale.GERMAN to "Werfe den Bot aus dem Sprachkanal",
                )
            )
}