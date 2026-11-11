/*
 * File:          Play.kt
 * Path:          v3\src\main\kotlin\commands\Play.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 12:52:43 +02:00
 *
 * SHA-256: d9adf7eb83addf724dfbd1137d1b3e49864656fe712cb840eb7637862ee2a1ff
 *
 */


package commands

import atenea.NodeManager.nodeManager
import atenea.driver.io.Link
import database.schema.Guild
import database.schema.Session
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.exceptions.PermissionException
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import player.Player.getMusicManager
import player.Player.loadAndPlay
import utils.Constants
import utils.Constants.formatEmoji

class Play: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        if (!event.isFromGuild || event.member == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_do")).setEphemeral(true).queue()
            return
        }

        val channel = event.member!!.voiceState?.channel
        val manager = getMusicManager(event.guild!!, event.channel as GuildMessageChannel, true)!!

        if (channel == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_vc")).setEphemeral(true)
                .queue()
            return
        }

        if (!channel.guild.selfMember.hasPermission(
                channel,
                Permission.VOICE_CONNECT
            ) || !channel.guild.selfMember.hasPermission(channel, Permission.VOICE_SPEAK)
        ) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_vc_perms")).setEphemeral(true)
                .queue()
            return
        }

        if (config.disabledVoiceChannels.contains(channel.id)) {
            if (!event.member!!.hasPermission(Permission.MANAGE_SERVER)) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.vc_disabled"))
                    .setEphemeral(true).queue()
                return
            }
        }

        var search = event.getOption("canción")?.asString
        if (search == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_song")).setEphemeral(true)
                .queue()
            return
        }

        if (Constants.TwitchRegex.matches(search) && !config.premiumEnabled) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.no_premium_twitch")).setEphemeral(true)
                .queue()
            return
        }

        if (channel.type == ChannelType.STAGE && !config.premiumEnabled) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.no_premium_stage")).setEphemeral(true)
                .queue()
            return
        }

        if (Constants.YouTubeRegex.matches(search)) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.youtube_link")).setEphemeral(true)
                .queue()
            return
        }

        val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
        if (session != null) {
            if (!isHost) {
                event.reply(formatEmoji(Constants.Emojis.error) + "No puedes modificar la cola de reproducción mientras estás en una sesión!")
                    .setEphemeral(true).queue()
                return
            }
        }

        if (nodeManager.nodes.filterNot { it.isAvailable }.isNotEmpty()) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_player")).setEphemeral(true)
                .queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.channel !== null) {
            if (event.guild!!.selfMember.voiceState!!.channel !== channel) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.different_vc"))
                    .setEphemeral(true).queue()
                return
            }
        } else {
            try {
                if(manager.link.state == Link.State.DESTROYED) manager.recreate()
                manager.link.connect(channel)
            } catch (e: PermissionException) {
                if (e.permission === Permission.VOICE_CONNECT) {
                    event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_vc_perms"))
                        .setEphemeral(true).queue()
                    return
                }
            } catch (e: Exception) {
                event.reply(formatEmoji(Constants.Emojis.error) + "Woops, thats an error!").setEphemeral(true).queue()
                return
            }
        }

        if(!manager.link.player.isConnected)
            manager.link.connect(channel)

        event.guild!!.audioManager.isSelfDeafened = true

        var isSearch = false
        if (
            !Constants.SpotifyRegex.matches(search) &&
            !Constants.SoundCloudRegex.matches(search) &&
            !Constants.TwitchRegex.matches(search) &&
            !Constants.DeezerRegex.matches(search) &&
            !Constants.AppleMusicRegex.matches(search) &&
            !Constants.TikTokRegex.matches(search) &&
            !Constants.RedditRegex.matches(search)
        ) {
            search = "spsearch:$search"
            isSearch = true
        } else {
            search = search.replace(Regex("\\?.*"), "")
        }

        manager.channel = event.channel as GuildMessageChannel
        loadAndPlay(manager, event, search, isSearch, config, lang)
    }

    override val name: String
        get() = "play"
    override val description: String
        get() = "Reproduce una canción en el canal de voz!"
    override val category: String
        get() = "Music"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf()
    override val usage: String
        get() = "<Song>"
    override val premium: Boolean
        get() = false
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Reproduce una canción en el canal de voz!",
                    DiscordLocale.ENGLISH_US to "Play a song in the voice channel!",
                    DiscordLocale.ENGLISH_UK to "Play a song in the voice channel!",
                    DiscordLocale.FRENCH to "Joue une chanson dans le canal vocal!",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Toca uma música no canal de voz!",
                    DiscordLocale.ITALIAN to "Riproduci una canzone nel canale vocale!",
                    DiscordLocale.GERMAN to "Spiele einen Song im Sprachkanal!",
                )
            )
            .addOptions(
                OptionData(
                    OptionType.STRING,
                    "canción",
                    "Canción a reproducir, puede ser un link de Spotify, SoundCloud o un título a buscar!"
                )
                    .setNameLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "canción",
                            DiscordLocale.ENGLISH_US to "song",
                            DiscordLocale.ENGLISH_UK to "song",
                            DiscordLocale.FRENCH to "chanson",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "música",
                            DiscordLocale.ITALIAN to "canzone",
                            DiscordLocale.GERMAN to "song",
                        )
                    )
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Canción a reproducir, puede ser un link de Spotify, SoundCloud o un título a buscar!",
                            DiscordLocale.ENGLISH_US to "Song to play, can be a Spotify, SoundCloud link or a title to search!",
                            DiscordLocale.ENGLISH_UK to "Song to play, can be a Spotify, SoundCloud link or a title to search!",
                            DiscordLocale.FRENCH to "Chanson à jouer, peut être un lien Spotify, SoundCloud ou un titre à rechercher!",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Música para tocar, pode ser um link do Spotify, SoundCloud ou um título para pesquisar!",
                            DiscordLocale.ITALIAN to "Canzone da riprodurre, può essere un link di Spotify, SoundCloud o un titolo da cercare!",
                            DiscordLocale.GERMAN to "Song zum Spielen, kann ein Spotify-, SoundCloud-Link oder ein Titel zum Suchen sein!",
                        )
                    )
                    .setRequired(true)
                    .setAutoComplete(true)
            )
}