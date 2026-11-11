/*
 * File:          Volume.kt
 * Path:          v3\src\main\kotlin\commands\Volume.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-05-29 13:53:04 +02:00
 *
 * SHA-256: 3ccd974679a28d994f0207e88796737e408ebf51c81651e28d62d55ac9c1397b
 *
 */


package commands

import database.schema.Guild
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

class Volume: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {
        if (!config.premiumEnabled) {
            event.reply(lang.get("manager.no_premium")).setEphemeral(true).queue()
            return
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

        val player = manager.player
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

        if(player.playingTrack == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val option = event.getOption("nivel")
        if(option == null) {

            val embed = EmbedBuilder()
                .setColor(config.embedColor)
                .setDescription(lang.get("commands.volume.info").replace("%s%", (player.filters.volume * 100).toString()+"%"))

            event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            return
        }

        val nivel = option.asLong

        if(nivel > 100 || nivel < 0) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.volume.invalid_level")).setEphemeral(true).queue()
            return
        }

        val level = nivel.toFloat() / 100

        player.filters.setVolume(level)
        player.filters.commit()

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setDescription(lang.get("commands.volume.set").replace("%s%", "$nivel%"))

        event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()

    }

    override val name: String
        get() = "volume"
    override val description: String
        get() = "Establecer el volumen de la canción actual."
    override val category: String
        get() = "Music"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf()
    override val usage: String
        get() = "<New volume>"
    override val premium: Boolean
        get() = true
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Establecer el volumen de la canción actual.",
                    DiscordLocale.ENGLISH_US to "Set the volume of the current song.",
                    DiscordLocale.ENGLISH_UK to "Set the volume of the current song.",
                    DiscordLocale.FRENCH to "Définir le volume de la chanson actuelle.",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Defina o volume da música atual.",
                    DiscordLocale.ITALIAN to "Imposta il volume della canzone corrente.",
                    DiscordLocale.GERMAN to "Setzen Sie die Lautstärke des aktuellen Songs.",
                )
            )
            .addOptions(
                OptionData(OptionType.INTEGER, "nivel", "Nivel de volumen a establecer ( 0 - 100 )")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Nivel de volumen a establecer ( 0 - 100 )",
                            DiscordLocale.ENGLISH_US to "Volume level to set ( 0 - 100 )",
                            DiscordLocale.ENGLISH_UK to "Volume level to set ( 0 - 100 )",
                            DiscordLocale.FRENCH to "Niveau de volume à définir ( 0 - 100 )",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Nível de volume a definir ( 0 - 100 )",
                            DiscordLocale.ITALIAN to "Livello di volume da impostare ( 0 - 100 )",
                            DiscordLocale.GERMAN to "Lautstärkepegel einstellen ( 0 - 100 )",
                        )
                    )
                    .setRequired(false)
            )
}