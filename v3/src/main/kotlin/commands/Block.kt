/*
 * File:          Block.kt
 * Path:          v3\src\main\kotlin\commands\Block.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-05-29 13:53:04 +02:00
 *
 * SHA-256: b99ad1ae304d214887de4ef422bfda02ed6e5e8fabc8286ffe98f4edc287ca07
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
import utils.Constants.formatEmoji
import java.net.URL

class Block: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {
        if (!config.premiumEnabled) {
            event.reply(lang.get("manager.no_premium")).setEphemeral(true).queue()
            return
        }

        val link = event.getOption("link")?.asString
        if (link == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.block.no_link")).setEphemeral(true)
                .queue()
            return
        }

        try {
            URL(link)
        } catch (e: Exception) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.block.invalid_link")).setEphemeral(true)
                .queue()
            return
        }

        val formattedLink = link
            .replace("https://", "").replace("http://", "")
            .split("?")[0]

        if (config.blockedSongs.contains(formattedLink)) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.block.already_blocked"))
                .setEphemeral(true)
                .queue()
            return
        }

        if (
            !Constants.SpotifyRegex.matches(link) &&
            !Constants.SoundCloudRegex.matches(link) &&
            !Constants.TwitchRegex.matches(link) &&
            !Constants.DeezerRegex.matches(link) &&
            !Constants.AppleMusicRegex.matches(link) &&
            !Constants.TikTokRegex.matches(link) &&
            !Constants.RedditRegex.matches(link)
        ) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.block.invalid_link")).setEphemeral(true)
                .queue()
            return
        }

        config.blockedSongs = config.blockedSongs.plus(formattedLink)
        config.save()

        event.reply(formatEmoji(Constants.Emojis.success) + lang.get("commands.block.block")
            .replace("%s%", config.blockedSongs.size.toString())
            .replace("%t%", event.guild!!.id)
        ).queue()

    }

    override val name: String
        get() = "block"
    override val description: String
        get() = "Añadir una canción a la lista negra"
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
                    DiscordLocale.SPANISH to "Añade una canción a la lista negra",
                    DiscordLocale.ENGLISH_US to "Add a song to the blacklist",
                    DiscordLocale.ENGLISH_UK to "Add a song to the blacklist",
                    DiscordLocale.FRENCH to "Ajouter une chanson à la liste noire",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Adicionar uma música à lista negra",
                    DiscordLocale.ITALIAN to "Aggiungi una canzone alla blacklist",
                    DiscordLocale.GERMAN to "Füge einen Song zur Blacklist hinzu",
                )
            )
            .addOptions(
                OptionData(
                    OptionType.STRING,
                    "link",
                    "Link de la canción a añadir a la lista negra"
                ).setDescriptionLocalizations(
                    mapOf(
                        DiscordLocale.SPANISH to "Link de la canción a añadir a la lista negra",
                        DiscordLocale.ENGLISH_US to "Link of the song to add to the blacklist",
                        DiscordLocale.ENGLISH_UK to "Link of the song to add to the blacklist",
                        DiscordLocale.FRENCH to "Lien de la chanson à ajouter à la liste noire",
                        DiscordLocale.PORTUGUESE_BRAZILIAN to "Link da música para adicionar à lista negra",
                        DiscordLocale.ITALIAN to "Link della canzone da aggiungere alla blacklist",
                        DiscordLocale.GERMAN to "Link des Songs, der zur Blacklist hinzugefügt werden soll",
                    )
                )
                    .setRequired(true)
            )
    override val premium: Boolean
        get() = true
}

/*
* todo:
*  - cuando se borra un código tambien se desactiva el premium de un servidor
*/