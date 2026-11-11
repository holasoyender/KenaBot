/*
 * File:          Premium.kt
 * Path:          v3\src\main\kotlin\commands\Premium.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: 5f69289dba3dd51d2ec4b304cc9c4aabed73a4b7231ce954e7a95a4f336669c7
 *
 */


package commands

import database.schema.Guild
import database.schema.Premium
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.utils.TimeFormat
import utils.Constants
import utils.Constants.formatEmoji
import utils.Time

class Premium: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        val code = event.getOption("code")?.asString

        if (code == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.premium.no_code")).setEphemeral(true)
                .queue()
            return
        }

        val premium = Premium.get(code)
        if (premium == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.premium.invalid_code"))
                .setEphemeral(true).queue()
            return
        }

        if (premium.claimed) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.premium.used_code")).setEphemeral(true)
                .queue()
            return
        }

        if (premium.claimedAt.isNotBlank() && premium.claimedAt != event.guild!!.id) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.premium.invalid_code"))
                .setEphemeral(true).queue()
            return
        }

        val bots = premium.bots.toMutableList()
        if (bots.contains(event.jda.selfUser.id)) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.premium.bot_claimed"))
                .setEphemeral(true).queue()
            return
        }

        if (config.premiumEnabled) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.premium.already_premium"))
                .setEphemeral(true).queue()
            return
        }

        bots.add(event.jda.selfUser.id)

        val claimed = bots.size == 4
        val claimedAt = event.guild!!.id
        val claimedOn = premium.claimedOn ?: System.currentTimeMillis()
        val time = Time.ms(premium.duration) + claimedOn

        if (time < System.currentTimeMillis()) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.premium.invalid_code") + " (Expired)")
                .setEphemeral(true).queue()
            return
        }

        premium.claimed = claimed
        premium.claimedAt = claimedAt
        premium.claimedOn = claimedOn
        premium.bots = bots

        premium.save()

        if (premium.duration == "Forever") {
            config.premiumEnabled = true
            config.premiumExpires = "Forever"
        } else {
            config.premiumEnabled = true
            config.premiumExpires = time
        }
        config.save()

        event.reply(
            lang.get("commands.premium.enabled").replace(
                "%t%",
                premium.duration + if (premium.duration != "Forever") "\n" + TimeFormat.RELATIVE.format(
                    time
                ) else ""
            )
        ).setEphemeral(true).queue()

    }

    override val name: String
        get() = "premium"
    override val description: String
        get() = "Activar el modo donador con un código"
    override val category: String
        get() = "Bot"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf(Permission.MANAGE_SERVER)
    override val usage: String
        get() = "<Code>"
    override val premium: Boolean
        get() = false
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Activar el modo donador con un código",
                    DiscordLocale.ENGLISH_US to "Enable premium mode with a code",
                    DiscordLocale.ENGLISH_UK to "Enable premium mode with a code",
                    DiscordLocale.FRENCH to "Activer le mode premium avec un code",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Ative o modo premium com um código",
                    DiscordLocale.ITALIAN to "Abilita la modalità premium con un codice",
                    DiscordLocale.GERMAN to "Aktivieren Sie den Premium-Modus mit einem Code",
                )
            )
            .addOptions(
                OptionData(OptionType.STRING, "code", "Código de donador")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Código de donador",
                            DiscordLocale.ENGLISH_US to "Premium code",
                            DiscordLocale.ENGLISH_UK to "Premium code",
                            DiscordLocale.FRENCH to "Code premium",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Código premium",
                            DiscordLocale.ITALIAN to "Codice premium",
                            DiscordLocale.GERMAN to "Premium-Code",
                        )
                    )
                    .setRequired(true)
            )
}