/*
 * File:          247.kt
 * Path:          v3\src\main\kotlin\commands\247.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: c1ce948fb723cca954e180bda07e1bde69f5a0f727b8312335a9afc0ed0ce0a7
 *
 */


package commands

import database.schema.Guild
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import player.Player
import utils.Constants
import utils.Constants.formatEmoji

class `247`: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {
        if (!config.premiumEnabled) {
            event.reply(lang.get("manager.no_premium")).setEphemeral(true).queue()
            return
        }

        val active = event.getOption("activado")?.asString == "on"
        val channel = event.getOption("canal")?.asChannel

        if(active) {

            if(channel == null) {
                event.reply(lang.get("commands._247.no_channel")).setEphemeral(true).queue()
                return
            }

            if(channel.type != ChannelType.VOICE && channel.type != ChannelType.STAGE) {
                event.reply(lang.get("commands._247.no_channel")).setEphemeral(true).queue()
                return
            }

            config.radioEnabled = true
            config.radioChannelID = channel.id

            config.save()

            event.reply(formatEmoji(Constants.Emojis.success) + lang.get("commands._247.enabled").replace("%s%", channel.asMention)).setEphemeral(false).queue()

            event.guild!!.audioManager.openAudioConnection(channel as AudioChannel)
            event.guild!!.audioManager.isSelfDeafened = true

        } else {

            config.radioEnabled = false
            config.radioChannelID = ""

            config.save()

            val manager = Player.getMusicManager(event.guild!!)

            if (manager != null) {
                val player = manager.player

                if(player.playingTrack == null) {

                    player.stopTrack()
                    player.isPaused = false

                    manager.link.destroy()
                    Player.destroyMusicManager(event.guild!!)

                    try {
                        event.guild!!.audioManager.closeAudioConnection()
                    } catch (e: Exception) {
                        //ignore
                    }
                }
            }
            event.reply(formatEmoji(Constants.Emojis.success) + lang.get("commands._247.disabled")).setEphemeral(false).queue()

        }
    }

    override val name: String
        get() = "247"
    override val description: String
        get() = "Establecer un canal de 24/7."
    override val category: String
        get() = "Music"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf(Permission.MANAGE_SERVER)
    override val usage: String
        get() = "<Activar/Desactivar> <Canal>"
    override val premium: Boolean
        get() = true
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Establecer un canal de 24/7.",
                    DiscordLocale.ENGLISH_US to "Set a 24/7 channel.",
                    DiscordLocale.ENGLISH_UK to "Set a 24/7 channel.",
                    DiscordLocale.FRENCH to "Définir un canal 24/7.",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Defina um canal 24/7.",
                    DiscordLocale.ITALIAN to "Imposta un canale 24/7.",
                    DiscordLocale.GERMAN to "Setzen Sie einen 24/7-Kanal.",
                )
            )
            .addOptions(
                OptionData(OptionType.STRING, "activado", "Activar o desactivar el modo 24/7.")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Activar o desactivar el modo 24/7.",
                            DiscordLocale.ENGLISH_US to "Enable or disable 24/7 mode.",
                            DiscordLocale.ENGLISH_UK to "Enable or disable 24/7 mode.",
                            DiscordLocale.FRENCH to "Activer ou désactiver le mode 24/7.",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Ativar ou desativar o modo 24/7.",
                            DiscordLocale.ITALIAN to "Abilita o disabilita la modalità 24/7.",
                            DiscordLocale.GERMAN to "Aktivieren oder Deaktivieren Sie den 24/7-Modus.",
                        )
                    )
                    .setRequired(true)
                    .addChoice("Activar", "on")
                    .addChoice("Desactivar", "off"),
                OptionData(OptionType.CHANNEL, "canal", "Canal de voz para el modo 24/7.").setRequired(false)
            )
}