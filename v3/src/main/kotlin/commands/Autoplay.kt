/*
 * File:          Autoplay.kt
 * Path:          v3\src\main\kotlin\commands\Autoplay.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-05-29 13:53:04 +02:00
 *
 * SHA-256: fa0cd40f088671a61336ed2a16e99b726e05219b84f50c0039433fe0ecc1b69a
 *
 */


package commands

import database.schema.Guild
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import utils.Constants

class Autoplay: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {
        if (!config.premiumEnabled) {
            event.reply(lang.get("manager.no_premium")).setEphemeral(true).queue()
            return
        }

        val option = event.getOption("estado")?.asLong
        if (option == null) {
            event.reply(lang.get("commands.autoplay.no_state")).setEphemeral(true).queue()
            return
        }

        /*if (!config.premiumEnabled) {
            event.reply(lang.get("manager.no_premium")).setEphemeral(true).queue()
            return
        }*/

        if (option !in 0L..1L) {
            event.reply("${Constants.Emojis.error}  ${lang.get("commands.autoplay.invalid_state")}").setEphemeral(true).queue()
            return
        }

        if (config.autoplay == (option == 0L)) {
            event.reply("${Constants.Emojis.error}  ${lang.get("commands.autoplay.already_in").replace("%s%",
                when (option) {
                    0L -> lang.get("commands.autoplay.enabled")
                    1L -> lang.get("commands.autoplay.disabled")
                    else -> lang.get("commands.autoplay.unknown")
                }
                )}").setEphemeral(true).queue()
            return
        }

        config.autoplay = option == 0L
        config.save()

        event.reply("${Constants.Emojis.success}  ${lang.get("commands.autoplay.success").replace("%s%",
            when (option) {
                0L -> lang.get("commands.autoplay.enabled")
                1L -> lang.get("commands.autoplay.disabled")
                else -> lang.get("commands.autoplay.unknown")
            }
        )}").queue()

    }

    override val name: String
        get() = "autoplay"
    override val description: String
        get() = "Activar o desactivar el modo de reproducción automática."
    override val category: String
        get() = "Music"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf(Permission.MANAGE_SERVER)
    override val usage: String
        get() = "[enable|disable]"
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Activar o desactivar el modo de reproducción automática.",
                    DiscordLocale.ENGLISH_US to "Enable or disable the autoplay mode.",
                    DiscordLocale.ENGLISH_UK to "Enable or disable the autoplay mode.",
                    DiscordLocale.FRENCH to "Activer ou désactiver le mode de lecture automatique.",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Ativar ou desativar o modo de reprodução automática.",
                    DiscordLocale.ITALIAN to "Abilita o disabilita la modalità di riproduzione automatica.",
                    DiscordLocale.GERMAN to "Aktivieren oder Deaktivieren Sie den Autoplay-Modus.",
                )
            )
            .addOptions(
                OptionData(OptionType.INTEGER, "estado", "Estado del autoplay")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Estado del autoplay",
                            DiscordLocale.ENGLISH_US to "Autoplay state",
                            DiscordLocale.ENGLISH_UK to "Autoplay state",
                            DiscordLocale.FRENCH to "État de la lecture automatique",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Estado de reprodução automática",
                            DiscordLocale.ITALIAN to "Stato di riproduzione automatica",
                            DiscordLocale.GERMAN to "Autoplay-Status",
                        )
                    )
                    .addChoice("On", 0)
                    .addChoice("Off", 1)
                    .setRequired(true)
            )
    override val premium: Boolean
        get() = true
}