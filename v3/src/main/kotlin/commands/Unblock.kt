/*
 * File:          Unblock.kt
 * Path:          v3\src\main\kotlin\commands\Unblock.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 6e488cd393ebf3eb316ee6eeb7d839488d5c3d0d366ed53d5d456f5e2dda663f
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
import java.net.URL

class Unblock: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        val link = event.getOption("link")?.asString
        if (link == null) {
            event.reply(Constants.formatEmoji(Constants.Emojis.error) + lang.get("commands.block.no_link")).setEphemeral(true)
                .queue()
            return
        }

        try {
            URL(link)
        } catch (e: Exception) {
            event.reply(Constants.formatEmoji(Constants.Emojis.error) + lang.get("commands.block.invalid_link")).setEphemeral(true)
                .queue()
            return
        }

        val formattedLink = link
            .replace("https://", "").replace("http://", "")
            .split("?")[0]

        if (!config.blockedSongs.contains(formattedLink)) {
            event.reply(Constants.formatEmoji(Constants.Emojis.error) + lang.get("commands.block.not_blocked"))
                .setEphemeral(true)
                .queue()
            return
        }

        config.blockedSongs = config.blockedSongs.filter { it != formattedLink }.toMutableList()
        config.save()

        event.reply(Constants.formatEmoji(Constants.Emojis.success) + lang.get("commands.block.unblock")
            .replace("%s%", config.blockedSongs.size.toString())
            .replace("%t%", event.guild!!.id)
        ).queue()

    }

    override val name: String
        get() = "unblock"
    override val description: String
        get() = "Quitar una canción de la lista negra"
    override val category: String
        get() = "Bot"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf(Permission.MANAGE_SERVER)
    override val usage: String
        get() = "<Song link>"
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Quita una canción de la lista negra",
                    DiscordLocale.ENGLISH_US to "Remove a song from the blacklist",
                    DiscordLocale.ENGLISH_UK to "Remove a song from the blacklist",
                    DiscordLocale.FRENCH to "Retirer une chanson de la liste noire",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Remover uma música da lista negra",
                    DiscordLocale.ITALIAN to "Rimuovi una canzone dalla blacklist",
                    DiscordLocale.GERMAN to "Entfernen Sie einen Song von der Blacklist",
                )
            )
            .addOptions(
                OptionData(
                    OptionType.STRING,
                    "link",
                    "Link de la canción a quitar de la lista negra"
                ).setDescriptionLocalizations(
                    mapOf(
                        DiscordLocale.SPANISH to "Link de la canción a quitar de la lista negra",
                        DiscordLocale.ENGLISH_US to "Link of the song to remove from the blacklist",
                        DiscordLocale.ENGLISH_UK to "Link of the song to remove from the blacklist",
                        DiscordLocale.FRENCH to "Lien de la chanson à retirer de la liste noire",
                        DiscordLocale.PORTUGUESE_BRAZILIAN to "Link da música para remover da lista negra",
                        DiscordLocale.ITALIAN to "Link della canzone da rimuovere dalla blacklist",
                        DiscordLocale.GERMAN to "Link des Songs, der von der Blacklist entfernt werden soll",
                    )
                )
                    .setRequired(true)
            )
    override val premium: Boolean
        get() = true
}