/*
 * File:          Support.kt
 * Path:          v3\src\main\kotlin\commands\Support.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 05905780b8acaac052d766416ebc0a376331ff4af24d2e8e45c2720c47b1b36f
 *
 */


package commands

import database.schema.Guild
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import utils.Constants
import utils.Constants.formatEmoji

class Support: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        event.reply(formatEmoji(Constants.Emojis.success)+lang.get("commands.support")).setEphemeral(true).addActionRow(
            Button.link("https://kenabot.xyz/support",
                lang.get("commands.help.support.support"))
        ).queue()
    }

    override val name: String
        get() = "support"
    override val description: String
        get() = "Mandar una invitación al servidor de soporte del bot"
    override val category: String
        get() = "Bot"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = false
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
                    DiscordLocale.SPANISH to "Mandar una invitación al servidor de soporte del bot",
                    DiscordLocale.ENGLISH_US to "Send an invite to the bot's support server",
                    DiscordLocale.ENGLISH_UK to "Send an invite to the bot's support server",
                    DiscordLocale.FRENCH to "Envoyer une invitation au serveur de support du bot",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Enviar um convite para o servidor de suporte do bot",
                    DiscordLocale.ITALIAN to "Invia un invito al server di supporto del bot",
                    DiscordLocale.GERMAN to "Senden Sie eine Einladung zum Support-Server des Bots",
                )
            )

}