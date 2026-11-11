/*
 * File:          Ping.kt
 * Path:          v3\src\main\kotlin\commands\Ping.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 95462e3e5838bfff78f6021ee720cfa794c715f9e72ac2853d7dcc0e4739d886
 *
 */


package commands

import database.schema.Guild
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import utils.PingCalc


class Ping : Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {
        val jda: JDA = event.jda
        event.reply(":ping_pong:  Pong! `${jda.gatewayPing}ms (${PingCalc.end()}ms)`").setEphemeral(config.silent).queue()
    }

    override val name: String
        get() = "ping"
    override val description: String
        get() = "Mostrar el ping del bot"
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
                    DiscordLocale.SPANISH to "Mostrar el ping del bot",
                    DiscordLocale.ENGLISH_US to "Show the bot's ping",
                    DiscordLocale.ENGLISH_UK to "Show the bot's ping",
                    DiscordLocale.FRENCH to "Afficher le ping du bot",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Mostrar o ping do bot",
                    DiscordLocale.ITALIAN to "Mostra il ping del bot",
                    DiscordLocale.GERMAN to "Zeige den Bot-Ping"
                )
            )

}