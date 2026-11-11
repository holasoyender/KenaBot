/*
 * File:          Config.kt
 * Path:          v3\src\main\kotlin\commands\Config.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 91c86ef3cdfe3f864e07d38cf7ace129db02a34084cc6eb20c8887d15217a81c
 *
 */


package commands

import database.schema.Guild
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl

class Config: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setTitle(lang.get("commands.config.title"))
            .setDescription(lang.get("commands.config.description").replace("%s%", event.jda.selfUser.id))

        val premiumButton =
            if (config.premiumEnabled)
                Button.primary("cmd:config:premium:${event.user.id}", lang.get("commands.config.buttons.premium_on"))
            else
                Button.danger("cmd:config:premium:${event.user.id}", lang.get("commands.config.buttons.premium_off"))


        event.replyEmbeds(embed.build())
            .addActionRow(
            Button.secondary("cmd:config:lang:${event.user.id}", lang.get("commands.config.buttons.lang"))
                .withEmoji(Emoji.fromUnicode(lang.get("flag"))),
            Button.secondary("cmd:config:channels:${event.user.id}", lang.get("commands.config.buttons.channels"))
                .withEmoji(Emoji.fromCustom(CustomEmojiImpl("channels", 940254447530016859, false))),
            Button.secondary("cmd:config:dj:${event.user.id}", lang.get("commands.config.buttons.dj"))
                .withEmoji(Emoji.fromCustom(CustomEmojiImpl("mention", 940268589615812608, false))),
            Button.secondary("cmd:config:more:${event.user.id}", lang.get("commands.config.buttons.more"))
        ).addActionRow(
            Button.secondary("cmd:config:commands:${event.user.id}", lang.get("commands.config.buttons.commands"))
                .withEmoji(Emoji.fromCustom(CustomEmojiImpl("slash", 941024012270710874, false))),
            premiumButton,
            Button.danger("cmd:config:sync:${event.user.id}", Emoji.fromCustom(CustomEmojiImpl("loop", 952242523521294456, false))).withDisabled(event.jda.selfUser.name == "KenaBot")
        ).queue()
    }

    override val name: String
        get() = "config"
    override val description: String
        get() = "Menú de configuración del bot"
    override val category: String
        get() = "Bot"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf(Permission.MANAGE_SERVER)
    override val usage: String
        get() = ""
    override val premium: Boolean
        get() = false
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Menú de configuración del bot",
                    DiscordLocale.ENGLISH_US to "Bot configuration menu",
                    DiscordLocale.ENGLISH_UK to "Bot configuration menu",
                    DiscordLocale.FRENCH to "Menu de configuration du bot",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Menu de configuração do bot",
                    DiscordLocale.ITALIAN to "Menu di configurazione del bot",
                    DiscordLocale.GERMAN to "Bot-Konfigurationsmenü"
                )
            )
}