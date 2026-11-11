/*
 * File:          Artist.kt
 * Path:          v3\src\main\kotlin\commands\Artist.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 7088c7cfc5f3608324528f0b0651054c82c17ab862587441025be1522a51fc58
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
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.utils.FileUpload
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import thirdparty.spotify.SpotifyApi
import utils.Constants
import utils.Constants.formatEmoji
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat


class Artist: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        val artistName = event.getOption("artista")?.asString
        if (artistName == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.artist.no_artist")).setEphemeral(true)
                .queue()
            return
        }

        val artist = SpotifyApi.searchForArtist(artistName)
        if (artist == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.artist.no_artist")).setEphemeral(true)
                .queue()
            return
        }

        event.deferReply().queue { msg ->

            val formatter = DecimalFormat("#,###")
            val popularityEmoji = when (artist.popularity) {
                in 0..20 -> Constants.Emojis.popularity0
                in 21..40 -> Constants.Emojis.popularity1
                in 41..60 -> Constants.Emojis.popularity2
                in 61..80 -> Constants.Emojis.popularity3
                in 81..100 -> Constants.Emojis.popularity4
                else -> Constants.Emojis.popularity0
            }

            val embed = EmbedBuilder()
                .setColor(config.embedColor)
                .setAuthor(
                    artist.name,
                    artist.uri,
                    artist.getSmallestImage()?.url ?: event.jda.selfUser.effectiveAvatarUrl
                )
                .setTitle("${lang.get("commands.artist.embed_title")}  ${Constants.Emojis.beta1}${Constants.Emojis.beta2}")
                .setThumbnail(artist.getLargestImage()?.url ?: event.jda.selfUser.effectiveAvatarUrl)
                .setDescription("${artist.bio ?: lang.get("commands.artist.no_bio")}\n\n**${lang.get("commands.artist.genres")}:** ```${if (artist.genres.isNotEmpty()) artist.genres.joinToString(", ") else "?"}```")
                .addField(lang.get("commands.artist.followers"), formatter.format(artist.followers).replace(Regex(","), "."), true)
                .addField(lang.get("commands.artist.popularity"), "$popularityEmoji ${artist.popularity}", true)

            try {
                val topTracks = SpotifyApi.getArtistAlbums(artist.id)
                if (!topTracks.isNullOrEmpty()) {
                    val images = topTracks.mapNotNull { album -> album.images.getOrNull(0) }.map { image -> image.url }

                    val json = JSONObject()
                    json.put("images", images)

                    val api = "https://api.kenabot.xyz/images/artists-top-tracks/${
                        URLEncoder.encode(
                            artist.name,
                            StandardCharsets.UTF_8
                        )
                    }"
                    val client = OkHttpClient()
                    val request = okhttp3.Request.Builder()
                        .url(api)
                        .post(json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull()))
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .addHeader("Accept", "image/png")
                        .addHeader("Authorization", "56bad67b2ae2408ba54470b0e75a7605")
                        .build()

                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val image = response.body?.bytes()
                        if (image == null) {
                            msg.editOriginalEmbeds(embed.build()).setActionRow(
                                Button.link(artist.uri, lang.get("commands.artist.open_spotify")),
                                Button.primary("cmd:play:play:${artist.uri}", lang.get("commands.artist.play_top_tracks"))
                            ).queue()
                            return@queue
                        }

                        embed.setImage("attachment://image.png")
                        msg.editOriginalEmbeds(embed.build())
                            .setFiles(FileUpload.fromData(image.inputStream(), "image.png"))
                            .setActionRow(
                                Button.link(artist.uri, lang.get("commands.artist.open_spotify")),
                                Button.primary("cmd:play:play:${artist.uri}", lang.get("commands.artist.play_top_tracks"))
                            ).queue()
                        return@queue
                    } else println(response.body?.string())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            msg.editOriginalEmbeds(embed.build()).setActionRow(
                Button.link(artist.uri, lang.get("commands.artist.open_spotify")),
                Button.primary("cmd:play:${artist.uri}", lang.get("commands.artist.play_top_tracks"))
            ).queue()
        }
    }

    override val name: String
        get() = "artist"
    override val description: String
        get() = "Información sobre un artista de Spotify."
    override val category: String
        get() = "Music"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf()
    override val usage: String
        get() = "<artist>"
    override val metadata: SlashCommandData
        get() =  Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Información sobre un artista de Spotify.",
                    DiscordLocale.ENGLISH_US to "Information about a Spotify artist.",
                    DiscordLocale.ENGLISH_UK to "Information about a Spotify artist.",
                    DiscordLocale.FRENCH to "Informations sur un artiste Spotify.",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Informações sobre um artista do Spotify.",
                    DiscordLocale.ITALIAN to "Informazioni su un artista di Spotify.",
                    DiscordLocale.GERMAN to "Informationen zu einem Spotify-Künstler.",
                )
            )
            .addOptions(
                OptionData(
                    OptionType.STRING,
                    "artista",
                    "Nombre del artista a buscar."
                )
                    .setNameLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "artista",
                            DiscordLocale.ENGLISH_US to "artist",
                            DiscordLocale.ENGLISH_UK to "artist",
                            DiscordLocale.FRENCH to "artiste",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "artista",
                            DiscordLocale.ITALIAN to "artista",
                            DiscordLocale.GERMAN to "künstler",
                        )
                    )
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Nombre del artista a buscar.",
                            DiscordLocale.ENGLISH_US to "Name of the artist to search.",
                            DiscordLocale.ENGLISH_UK to "Name of the artist to search.",
                            DiscordLocale.FRENCH to "Nom de l'artiste à rechercher.",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Nome do artista a ser pesquisado.",
                            DiscordLocale.ITALIAN to "Nome dell'artista da cercare.",
                            DiscordLocale.GERMAN to "Name des Künstlers, nach dem gesucht werden soll.",
                        )
                    )
                    .setRequired(true)
            )
    override val premium: Boolean
        get() = false
}