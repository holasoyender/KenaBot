/*
 * File:          Stop.kt
 * Path:          v3\src\main\kotlin\commands\Stop.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-13 22:46:09 +02:00
 *
 * SHA-256: d390ffd7758b310488eda5bce82afd548e4f7e1576ab1b2c1ac001b78a00ca51
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
import player.Player.getMusicManager
import utils.Constants
import utils.Constants.formatEmoji

class Stop: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

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

        val manager = getMusicManager(event.guild!!) ?: return event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
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

        val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
        if (session != null) {
            if (!isHost) {
                val listener =
                    session.listeners.firstOrNull { it.guildId == event.guild!!.id && it.botName == event.guild!!.jda.selfUser.name }
                if (listener != null) {

                    val channel = event.guild!!.getTextChannelById(listener.channelId)
                    channel?.sendMessage(
                        formatEmoji(Constants.Emojis.gear) + lang.get("commands.session.session_leave")
                            .replace("%s%", session.inviteCode)
                    )
                        ?.queue({}, {})
                    session.listeners =
                        session.listeners.filter { it.guildId != listener.guildId }
                    session.save()
                }
            }



            session.listeners.forEach { listener ->
                val guildManager = getMusicManager(listener.guildId, true)!!
                val guild = event.jda.shardManager!!.getGuildById(listener.guildId) ?: return@forEach

                guildManager.scheduler.queue.clear()
                guildManager.scheduler.isRepeating = LoopMode.NONE
                guildManager.scheduler.skip(null, true)

                val channel = guild.getTextChannelById(listener.channelId)
                channel?.sendMessage(formatEmoji(Constants.Emojis.gear) + lang.get("commands.session.session_end").replace("%s%", session.inviteCode))
                    ?.queue({}, {})
            }

            event.channel.sendMessage(formatEmoji(Constants.Emojis.gear) + lang.get("commands.session.session_end").replace("%s%", session.inviteCode)).queue({}, {})
            session.delete()
        }

        scheduler.queue.clear()
        scheduler.isRepeating = LoopMode.NONE
        scheduler.skip(null, true)

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setDescription(lang.get("commands.stop"))

        event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()

    }

    override val name: String
        get() = "stop"
    override val description: String
        get() = "Finaliza la lista de reproducción y saca al bot del canal de voz."
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
                    DiscordLocale.SPANISH to "Finaliza la lista de reproducción y saca al bot del canal de voz.",
                    DiscordLocale.ENGLISH_US to "Stops the queue and removes the bot from the voice channel.",
                    DiscordLocale.ENGLISH_UK to "Stops the queue and removes the bot from the voice channel.",
                    DiscordLocale.FRENCH to "Arrête la file d'attente et retire le bot du canal vocal.",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Para a fila e remove o bot do canal de voz.",
                    DiscordLocale.ITALIAN to "Interrompe la coda e rimuove il bot dal canale vocale.",
                    DiscordLocale.GERMAN to "Stoppt die Warteschlange und entfernt den Bot aus dem Sprachkanal.",
                )
            )
}