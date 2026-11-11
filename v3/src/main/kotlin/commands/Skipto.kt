/*
 * File:          Skipto.kt
 * Path:          v3\src\main\kotlin\commands\Skipto.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: b6bbe1e7d185d489d47648627944dcd6fdc1ee88c0b73a387a3a0d41ce472b41
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
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import player.Player
import utils.Constants
import utils.Constants.formatEmoji

class Skipto : Command {
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

        val manager = Player.getMusicManager(event.guild!!) ?: return event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
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

        val number = event.getOption("canción")?.asInt
        if (number == null) {
            event.reply(
                formatEmoji(Constants.Emojis.error) + lang.get("commands.remove.invalid").replace("%s%", "Desconocido")
            )
                .setEphemeral(true).queue()
            return
        }
        if (number < 1) {
            event.reply(
                formatEmoji(Constants.Emojis.error) + lang.get("commands.remove.invalid")
                    .replace("%s%", number.toString())
            )
                .setEphemeral(true).queue()
            return
        }
        if (number > scheduler.queue.size) {
            event.reply(
                formatEmoji(Constants.Emojis.error) + lang.get("commands.remove.invalid")
                    .replace("%s%", number.toString())
            )
                .setEphemeral(true).queue()
            return
        }

        val index = number - 1

        val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
        if(session != null) {
            if(!isHost) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.session.cant_modify_session")).setEphemeral(true).queue()
                return
            }

            session.listeners.forEach { listener ->
                val guildManager = Player.getMusicManager(listener.guildId, true)!!

                val toRemove = guildManager.scheduler.queue.map { it }.toList().subList(0, index)
                guildManager.scheduler.queue.removeAll(toRemove.toSet())
                guildManager.scheduler.skip(guildManager.scheduler.lastChannel)
            }
        }


        val toRemove = scheduler.queue.map { it }.toList().subList(0, index)
        scheduler.queue.removeAll(toRemove.toSet())
        scheduler.skip(event.channel as GuildMessageChannel)

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setDescription(
                formatEmoji(Constants.Emojis.success) + lang.get("commands.skip_to")
                    .replace("%s%", (index + 1).toString())
            )

        event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()
    }

    override val name: String
        get() = "skipto"
    override val description: String
        get() = "Saltar a una canción de la lista de reproducción en específico."
    override val category: String
        get() = "Music"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf()
    override val usage: String
        get() = "<número>"
    override val premium: Boolean
        get() = false
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Saltar a una canción de la lista de reproducción en específico.",
                    DiscordLocale.ENGLISH_US to "Skip to a specific song in the queue.",
                    DiscordLocale.ENGLISH_UK to "Skip to a specific song in the queue.",
                    DiscordLocale.FRENCH to "Passer à une chanson spécifique dans la file d'attente.",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Pule para uma música específica na fila.",
                    DiscordLocale.ITALIAN to "Salta a una canzone specifica nella coda.",
                    DiscordLocale.GERMAN to "Springe zu einem bestimmten Lied in der Warteschlange."
                )
            )
            .addOptions(
                OptionData(OptionType.INTEGER, "canción", "Número de la canción a reproducir")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Número de la canción a reproducir",
                            DiscordLocale.ENGLISH_US to "Number of the song to play",
                            DiscordLocale.ENGLISH_UK to "Number of the song to play",
                            DiscordLocale.FRENCH to "Numéro de la chanson à jouer",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Número da música a tocar",
                            DiscordLocale.ITALIAN to "Numero della canzone da riprodurre",
                            DiscordLocale.GERMAN to "Nummer des zu spielenden Liedes"
                        )
                    )
                    .setRequired(true)

            )
}