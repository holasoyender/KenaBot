/*
 * File:          Lyrics.kt
 * Path:          v3\src\main\kotlin\commands\Lyrics.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: 541d1e7cd26ac22ef4bd60aa0e66622eae372d909f34facd3a897268bd1ea91e
 *
 */


package commands

import database.schema.Guild
import functions.StringFormat
import interfaces.Command
import langs.LangUtils
import lyrics.LyricsClient
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import player.Player
import utils.Constants
import utils.Constants.formatEmoji

class Lyrics: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        var search = event.getOption("canción")?.asString
        if (search == null) {

            if (event.guild!!.selfMember.voiceState?.channel == null) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_song")).setEphemeral(true)
                    .queue()
                return
            }

            val manager = Player.getMusicManager(event.guild!!) ?: return event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            val player = manager.player

            if (player.playingTrack == null) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_song")).setEphemeral(true)
                    .queue()
                return
            }

            search = player.playingTrack.info.title + " " + player.playingTrack.info.author
        }

        search = StringFormat.format(search)

        event.deferReply(false).queue {

            LyricsClient().getLyrics(search).thenAccept { song ->

                if (song == null) {
                    it.editOriginal(formatEmoji(Constants.Emojis.error) + lang.get("commands.lyrics.error")).queue()
                    return@thenAccept
                }

                val title = song.title
                val url = song.url
                val author = song.author
                //val image = song
                val lyrics = song.content

                if (lyrics.isEmpty()) {
                    it.editOriginal(formatEmoji(Constants.Emojis.error) + lang.get("commands.lyrics.error")).queue()
                    return@thenAccept
                }

                val lyricsToSend = if (lyrics.length > 2048) {
                    lyrics.substring(0, 2045) + "..."
                } else {
                    lyrics
                }

                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setAuthor("$title by $author", url, event.jda.selfUser.avatarUrl)
                    //.setThumbnail(image)
                    .setDescription("```${lyricsToSend.replace(Regex("\\[.*?]"), "")}```")

                it.editOriginalEmbeds(embed.build()).queue()
            }
            /*val result = GLA().search(search)

            val hits = result.hits

            if(hits.isEmpty()) {
                it.editOriginal(formatEmoji(Constants.Emojis.error) + lang.get("commands.lyrics.error")).queue()
                return@queue
            }

            val song = hits.first

            if(song == null) {
                it.editOriginal(formatEmoji(Constants.Emojis.error) + lang.get("commands.lyrics.error")).queue()
                return@queue
            }

            val title = song.title
            val url = song.url
            val author = song.artist.name
            val image = song.imageUrl
            val lyrics: String
            try {
                lyrics = song.fetchLyrics()
            } catch (e: Exception) {
                it.editOriginal(formatEmoji(Constants.Emojis.error) + "Ha ocurrido un error!").queue()
                return@queue
            }

            val divided = divideString(StringBuilder(lyrics.trim()))

            val embed = EmbedBuilder()
                .setColor(config.embedColor)
                .setAuthor("$title by $author", url, event.jda.selfUser.avatarUrl)
                .setThumbnail(image)
                .setDescription("```${divided.first().replace(Regex("\\[.*?]"), "").replace(Regex("\n\n"), "\n")}```")

            it.editOriginalEmbeds(embed.build()).queue()*/
        }
    }

    override val name: String
        get() = "lyrics"
    override val description: String
        get() = "Mostrar los lyrics de una canción"
    override val category: String
        get() = "Music"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf()
    override val usage: String
        get() = "<canción>"
    override val premium: Boolean
        get() = false
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Muestra los lyrics de una canción",
                    DiscordLocale.ENGLISH_US to "Shows the lyrics of a song",
                    DiscordLocale.ENGLISH_UK to "Shows the lyrics of a song",
                    DiscordLocale.FRENCH to "Affiche les paroles d'une chanson",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Mostra as letras de uma música",
                    DiscordLocale.ITALIAN to "Mostra le parole di una canzone",
                    DiscordLocale.GERMAN to "Zeigt die Lyrics eines Songs an",
                )
            )
            .addOptions(
                OptionData(OptionType.STRING, "canción", "Canción a buscar los lyrics.")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Canción a buscar los lyrics.",
                            DiscordLocale.ENGLISH_US to "Song to search for lyrics.",
                            DiscordLocale.ENGLISH_UK to "Song to search for lyrics.",
                            DiscordLocale.FRENCH to "Chanson à rechercher les paroles.",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Música para pesquisar as letras.",
                            DiscordLocale.ITALIAN to "Canzone da cercare le parole.",
                            DiscordLocale.GERMAN to "Song zum Suchen nach Lyrics.",
                        )
                    )
                    .setRequired(false)
            )
    /*
    private fun divideString(builder: StringBuilder): List<String> {
        val list: MutableList<String> = LinkedList()
        var str = builder.toString().trim { it <= ' ' }
        var stringBuilder = StringBuilder()

        while (str.isNotEmpty()) {
            val index = str.indexOf('\n')
            val line = if (index == -1) str else str.substring(0, index + 1)
            str = str.substring(line.length)
            if (str == "\n") {
                str = ""
            }

            if (stringBuilder.length + line.length > 2000) {
                list.add(stringBuilder.toString().trim { it <= ' ' })
                stringBuilder = StringBuilder()
            }
            stringBuilder.append(line)
        }

        if (stringBuilder.isNotEmpty()) {
            list.add(stringBuilder.toString().trim { it <= ' ' })
        }
        return list
    }*/
}