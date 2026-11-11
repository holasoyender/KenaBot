/*
 * File:          Back.kt
 * Path:          v3\src\main\kotlin\commands\Back.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: bc2c1c0dd9a1738fd2ee58a6013d751080ff1d88544575d4ffee5f74c0033b84
 *
 */


package commands

import database.schema.Guild
import database.schema.Session
import functions.DjRole
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import player.Player
import utils.Constants
import utils.Constants.formatEmoji

@Suppress("DuplicatedCode")
class Back: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

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

        if(scheduler.lastTrack == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.back.no_previous")).setEphemeral(true).queue()
            return
        }

        val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
        if(session != null) {
            if(!isHost) {
                event.reply(formatEmoji(Constants.Emojis.error) + "No puedes modificar la cola de reproducción mientras estás en una sesión!").setEphemeral(true).queue()
                return
            }

            session.listeners.forEach { listener ->
                val guildManager = Player.getMusicManager(listener.guildId, true)!!

                val track = guildManager.scheduler.lastTrack!!.makeClone()
                val queue = guildManager.scheduler.queue.toTypedArray()

                guildManager.scheduler.queue.clear()
                guildManager.scheduler.queue.add(track)
                guildManager.scheduler.queue.addAll(queue)

                guildManager.scheduler.skip(null)

            }
        }

        val track = scheduler.lastTrack!!.makeClone()
        val queue = scheduler.queue.toTypedArray()

        scheduler.queue.clear()
        scheduler.queue.add(track)
        scheduler.queue.addAll(queue)

        scheduler.skip(event.channel as GuildMessageChannel)

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setDescription(formatEmoji(Constants.Emojis.success) + lang.get("commands.back.success"))

        event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()

    }

    override val name: String
        get() = "back"
    override val description: String
        get() = "Ir a la canción anterior de la cola"
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
                    DiscordLocale.SPANISH to "Ir a la canción anterior de la cola",
                    DiscordLocale.ENGLISH_US to "Go to the previous song in the queue",
                    DiscordLocale.ENGLISH_UK to "Go to the previous song in the queue",
                    DiscordLocale.FRENCH to "Revenir à la chanson précédente dans la file d'attente",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Voltar para a música anterior na fila",
                    DiscordLocale.ITALIAN to "Torna alla canzone precedente nella coda",
                    DiscordLocale.GERMAN to "Gehe zum vorherigen Lied in der Warteschlange"
                )
            )
}
