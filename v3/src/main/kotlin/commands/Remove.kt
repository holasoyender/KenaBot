/*
 * File:          Remove.kt
 * Path:          v3\src\main\kotlin\commands\Remove.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: 15f929ef06083d87fff612d7180832775b730d38b49349fddb1827bcaadbd1d2
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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import player.Player
import utils.Constants
import utils.Constants.formatEmoji

class Remove: Command {
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

        val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
        if(session != null) {
            if(!isHost) {
                event.reply(formatEmoji(Constants.Emojis.error) + "No puedes modificar la cola de reproducción mientras estás en una sesión!").setEphemeral(true).queue()
                return
            }

            session.listeners.forEach { listener ->
                val guildManager = Player.getMusicManager(listener.guildId, true)!!

                val song = guildManager.scheduler.queue.map { it }.toList()[number - 1]
                guildManager.scheduler.queue.remove(song)

            }
        }

        val song = scheduler.queue.map { it }.toList()[number - 1]

        scheduler.queue.remove(song)

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setDescription(
                formatEmoji(Constants.Emojis.success) + lang.get("commands.remove.success")
                    .replace("%s%", song.info.title)
            )

        event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()

    }

    override val name: String
        get() = "remove"
    override val description: String
        get() = "Quitar una canción de la lista de reproducción."
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
                    DiscordLocale.SPANISH to "Quitar una canción de la lista de reproducción.",
                    DiscordLocale.ENGLISH_US to "Remove a song from the queue.",
                    DiscordLocale.ENGLISH_UK to "Remove a song from the queue.",
                    DiscordLocale.FRENCH to "Supprimez une chanson de la file d'attente.",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Remova uma música da fila.",
                    DiscordLocale.ITALIAN to "Rimuovi una canzone dalla coda.",
                    DiscordLocale.GERMAN to "Entfernen Sie einen Song aus der Warteschlange.",
                )
            )
            .addOptions(
                OptionData(OptionType.INTEGER, "canción", "Número de la canción a quitar")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Número de la canción a quitar",
                            DiscordLocale.ENGLISH_US to "Number of the song to remove",
                            DiscordLocale.ENGLISH_UK to "Number of the song to remove",
                            DiscordLocale.FRENCH to "Numéro de la chanson à supprimer",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Número da música a remover",
                            DiscordLocale.ITALIAN to "Numero della canzone da rimuovere",
                            DiscordLocale.GERMAN to "Nummer des zu entfernenden Songs",
                        )
                    )
                    .setRequired(true)
            )
}