/*
 * File:          Commands.kt
 * Path:          v3\src\main\kotlin\commands\Commands.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 286d137807143bfb270ba415a5c730b164d0314095ae3e896ea4d5d7949e23b7
 *
 */


package commands

import database.schema.Guild
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.components.buttons.Button

class Commands: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        val embed: EmbedBuilder = EmbedBuilder()
            .setColor(config.embedColor)
            .setAuthor(event.jda.selfUser.name, null, event.jda.selfUser.avatarUrl)
            .setDescription(lang.get("commands.commands"))

        event.replyEmbeds(embed.build()).addActionRow(
            Button.link("https://kenabot.xyz/commands", lang.get("commands.help.commands.title"))
        ).queue()
    }

    override val name: String
        get() = "commands"
    override val description: String
        get() = "Lista de comandos del bot"
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
                    DiscordLocale.SPANISH to "Lista de comandos del bot",
                    DiscordLocale.ENGLISH_US to "List of bot commands",
                    DiscordLocale.ENGLISH_UK to "List of bot commands",
                    DiscordLocale.FRENCH to "Liste des commandes du bot",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Lista de comandos do bot",
                    DiscordLocale.ITALIAN to "Elenco dei comandi del bot",
                    DiscordLocale.GERMAN to "Liste der Botbefehle"
                )
            )
}