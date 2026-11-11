/*
 * File:          Help.kt
 * Path:          v3\src\main\kotlin\commands\Help.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 3f89904056bc8521ef4099bd59d0e74eeb30312db55f2ae9a74c3bc453c74a06
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

class Help : Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        val embed:EmbedBuilder = EmbedBuilder()
            .setColor(config.embedColor)
            .setAuthor(event.jda.selfUser.name, null, event.jda.selfUser.avatarUrl)
            .setDescription(lang.get("commands.help.desc").replace("%s%", event.jda.selfUser.name).replace("%i%", event.jda.selfUser.id))

        event.replyEmbeds(embed.build()).addActionRow(
            Button.link("https://kenabot.xyz/commands", lang.get("commands.help.commands.title")),
            Button.link("https://discord.com/api/oauth2/authorize?client_id=${event.jda.selfUser.id}&permissions=3534272&scope=bot%20applications.commands", lang.get("commands.help.add.title")),
            Button.link("https://kenabot.xyz/support", lang.get("commands.help.support.title"))
        ).queue()

    }

    override val name: String
        get() = "help"
    override val description: String
        get() = "Información útil sobre el bot"
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
                    DiscordLocale.SPANISH to "Información útil sobre el bot",
                    DiscordLocale.ENGLISH_US to "Useful information about the bot",
                    DiscordLocale.ENGLISH_UK to "Useful information about the bot",
                    DiscordLocale.FRENCH to "Informations utiles sur le bot",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Informações úteis sobre o bot",
                    DiscordLocale.ITALIAN to "Informazioni utili sul bot",
                    DiscordLocale.GERMAN to "Nützliche Informationen zum Bot"
                )
            )
}