/*
 * File:          Invite.kt
 * Path:          v3\src\main\kotlin\commands\Invite.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: b8ed2995c2556dd612b265e4f7e10219ab2c8b7969cd1b18f59cbbf091a092c1
 *
 */


package commands

import database.schema.Guild
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.components.buttons.Button

class Invite: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {
        event.reply(lang.get("commands.invite")).setEphemeral(true).addActionRow(
            Button.link(
                "https://discord.com/api/oauth2/authorize?client_id=${event.jda.selfUser.id}&permissions=3534272&scope=bot%20applications.commands",
                lang.get("commands.help.add.add")
            )
                .withEmoji(Emoji.fromFormatted("<:add:952249506680959026>"))
        ).queue()
    }

    override val name: String
        get() = "invite"
    override val description: String
        get() = "Mandar una invitación para añadirme a tu servidor"
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
                    DiscordLocale.SPANISH to "Mandar una invitación para añadirme a tu servidor",
                    DiscordLocale.ENGLISH_US to "Send an invitation to add me to your server",
                    DiscordLocale.ENGLISH_UK to "Send an invitation to add me to your server",
                    DiscordLocale.FRENCH to "Envoyer une invitation pour m'ajouter à votre serveur",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Enviar um convite para adicionar-me ao seu servidor",
                    DiscordLocale.ITALIAN to "Invia un invito per aggiungere il bot al tuo server",
                    DiscordLocale.GERMAN to "Senden Sie eine Einladung, um mich Ihrem Server hinzuzufügen"
                )
            )
}