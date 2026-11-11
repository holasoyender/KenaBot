/*
 * File:          Dashboard.kt
 * Path:          v3\src\main\kotlin\commands\Dashboard.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: ddf9db0fdacd575d4b7d38041879dc8cbd3e7257eb92b589293a21617dd59c48
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

class Dashboard: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {
        event.reply("La dashboard se encuentra actualmente en desarrollo, para configurarme puedes usar el comando `/config`!").setEphemeral(true).queue()
    }

    override val name: String
        get() = "dashboard"
    override val description: String
        get() = "Mandar un link a la dashboard de KenaBot"
    override val category: String
        get() = "Bot"
    override val enabled: Boolean
        get() = false
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
                    DiscordLocale.SPANISH to "Mandar un link a la dashboard de KenaBot",
                    DiscordLocale.ENGLISH_US to "Send a link to the KenaBot dashboard",
                    DiscordLocale.ENGLISH_UK to "Send a link to the KenaBot dashboard",
                    DiscordLocale.FRENCH to "Envoyer un lien vers le tableau de bord de KenaBot",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Enviar um link para o painel do KenaBot",
                    DiscordLocale.ITALIAN to "Invia un link alla dashboard di KenaBot",
                    DiscordLocale.GERMAN to "Senden Sie einen Link zur KenaBot-Dashboard"
                )
            )
}