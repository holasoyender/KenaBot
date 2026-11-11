/*
 * File:          Player.kt
 * Path:          v3\src\main\kotlin\player\Player.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-16 12:25:04 +02:00
 *
 * SHA-256: 12337bd2435d22a928577412af74feecdbf26704ab2c990ba53b5cb70a672d52
 *
 */


package player

import Main
import atenea.NodeManager.nodeManager
import com.neovisionaries.i18n.CountryCode
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import database.schema.Session
import functions.StringFormat
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import org.json.JSONObject
import player.applemusic.AppleMusicSourceManager
import player.deezer.DeezerAudioSourceManager
import player.reddit.RedditAudioSourceManager
import player.spotify.SpotifyAudioTrack
import player.spotify.SpotifySourceManager
import player.tiktok.TikTokAudioSourceManager
import thirdparty.errors.Error
import thirdparty.spotify.RotatingKeys
import utils.Constants
import utils.Constants.formatEmoji
import utils.PlayerTimestamp
import java.util.function.Consumer
import javax.print.attribute.standard.Severity
import kotlin.time.Duration.Companion.milliseconds

object Player {

    var musicManagers: HashMap<String, GuildMusicManager> = HashMap()
    private var playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
    var spotifySourceManager: SpotifySourceManager

    init {

        playerManager.enableGcMonitoring()
        playerManager.configuration.isFilterHotSwapEnabled = true
        playerManager.configuration.opusEncodingQuality = 1
        playerManager.configuration.resamplingQuality = AudioConfiguration.ResamplingQuality.LOW

        spotifySourceManager = SpotifySourceManager(null, CountryCode.ES.name, playerManager)

        playerManager.registerSourceManager(spotifySourceManager)
        playerManager.registerSourceManager(AppleMusicSourceManager(null, null, "ES", playerManager))
        playerManager.registerSourceManager(DeezerAudioSourceManager("05e23efdae9f4e553c184dd141187b3fae35c3f962b409c6188b8aeb3767452967239c66c3e7296b687addaa1f8dc34b20b3ece16c241817953bbee07fc70fe4a355237daca1006978731f014ab2859ce772be5bf98caa5456cc76ed863b8562"))
        playerManager.registerSourceManager(YoutubeAudioSourceManager(true, "", ""))
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault())
        playerManager.registerSourceManager(BandcampAudioSourceManager())
        playerManager.registerSourceManager(TwitchStreamAudioSourceManager())
        playerManager.registerSourceManager(TikTokAudioSourceManager())
        playerManager.registerSourceManager(RedditAudioSourceManager())

        musicManagers = HashMap()
    }

    private const val DEFAULT_VOLUME = 0.5f
    private const val MAX_QUEUE_SIZE = 1000

    fun loadAndPlay(
        manager: GuildMusicManager,
        event: SlashCommandInteractionEvent,
        query: String,
        isSearch: Boolean,
        config: database.schema.Guild,
        lang: LangUtils
    ) {

        playerManager.loadItemOrdered(
            manager,
            query,
            object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {

                    if (config.maxSongDuration > 0 && track.duration > config.maxSongDuration) {
                        event.reply("${Constants.Emojis.warning}  ${lang.get("manager.max_duration")}  (**${config.maxSongDuration.milliseconds}**)")
                            .setEphemeral(true).queue()
                        return
                    }

                    if (config.blockedSongs.contains(track.info.uri)) {
                        event.reply("${Constants.Emojis.warning}  ${lang.get("manager.blocked_song")}")
                            .setEphemeral(true).queue()
                        return
                    }

                    if (track is SpotifyAudioTrack && track.explicit && config.explicitFilter) {
                        event.reply("${Constants.Emojis.warning}  ${lang.get("manager.explicit_song")}")
                            .setEphemeral(true).queue()
                        return
                    }

                    val embed = EmbedBuilder()
                        .setColor(config.embedColor)
                        .setDescription(
                            lang.get("manager.song_added")
                                .replace("%s%", StringFormat.format(track.info.title))
                                .replace("%l%", track.info.uri ?: "https://kenabot.xyz")
                                .replace("%t%", PlayerTimestamp.get(track.duration)),
                        )

                    track.userData = event.member
                    manager.scheduler.queue(track)
                    manager.channel = event.channel as GuildMessageChannel

                    val (_, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
                    session?.listeners?.forEach { listener ->
                        val guildManager = getMusicManager(listener.guildId, true)
                        guildManager!!.scheduler.queue(track.makeClone())
                    }

                    event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {

                    val firstTrack = playlist.selectedTrack ?: playlist.tracks[0]
                    val tracks = playlist.tracks

                    if (isSearch) {

                        if (config.maxSongDuration > 0 && firstTrack.duration > config.maxSongDuration) {
                            event.reply("${Constants.Emojis.warning}  ${lang.get("manager.max_duration")}  (**${config.maxSongDuration.milliseconds}**)")
                                .setEphemeral(true).queue()
                            return
                        }

                        if (config.blockedSongs.contains(firstTrack.info.uri)) {
                            event.reply("${Constants.Emojis.warning}  ${lang.get("manager.blocked_song")}")
                                .setEphemeral(true).queue()
                            return
                        }

                        if (firstTrack is SpotifyAudioTrack && firstTrack.explicit && config.explicitFilter) {
                            event.reply("${Constants.Emojis.warning}  ${lang.get("manager.explicit_song")}")
                                .setEphemeral(true).queue()
                            return
                        }

                        val embed = EmbedBuilder()
                            .setColor(config.embedColor)
                            .setDescription(
                                lang.get("manager.song_added")
                                    .replace("%s%", StringFormat.format(firstTrack.info.title ?: "No title"))
                                    .replace("%l%", firstTrack.info.uri ?: "https://kenabot.xyz")
                                    .replace("%t%", PlayerTimestamp.get(firstTrack.duration)),
                            )

                        firstTrack.userData = event.member
                        manager.scheduler.queue(firstTrack)
                        manager.channel = event.channel as GuildMessageChannel

                        val (_, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
                        session?.listeners?.forEach { listener ->
                            val guildManager = getMusicManager(listener.guildId, true)
                            guildManager!!.scheduler.queue(firstTrack.makeClone())
                        }

                        event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()

                    } else {

                        if (
                            !Constants.SpotifyRegex.matches(query) &&
                            !Constants.SoundCloudRegex.matches(query) &&
                            !Constants.TwitchRegex.matches(query) &&
                            !Constants.DeezerRegex.matches(query) &&
                            !Constants.AppleMusicRegex.matches(query)
                        ) {
                            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.youtube_link"))
                                .setEphemeral(config.silent)
                                .queue()
                            return
                        }

                        if (!config.premiumEnabled && (manager.scheduler.queue.size + playlist.tracks.size > MAX_QUEUE_SIZE)) {
                            event.reply(
                                "${Constants.Emojis.warning}  ${
                                    lang.get("manager.max_queue_size").replace("%s%", "$MAX_QUEUE_SIZE")
                                }",
                            ).setEphemeral(true).queue()
                            return
                        }

                        val embed: EmbedBuilder = EmbedBuilder()
                            .setColor(config.embedColor)
                            .setDescription(
                                lang.get("manager.playlist_added").replace(
                                    "%s%",
                                    playlist.tracks.filter {
                                        !(config.maxSongDuration > 0 && it.duration > config.maxSongDuration) && !config.blockedSongs.contains(
                                            it.info.uri,
                                        )
                                    }.size.toString(),
                                ),
                            )

                        event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()

                        manager.scheduler.playlistSizeCache = playlist.tracks.size - 1

                        tracks.forEach(
                            Consumer { track: AudioTrack ->

                                if (config.maxSongDuration > 0 && track.duration > config.maxSongDuration) return@Consumer
                                if (config.blockedSongs.contains(track.info.uri)) return@Consumer
                                if (track is SpotifyAudioTrack && track.explicit && config.explicitFilter) return@Consumer

                                track.userData = event.member
                                manager.scheduler.queue(track)
                            },
                        )

                        val (_, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
                        session?.listeners?.forEach { listener ->
                            val guildManager = getMusicManager(listener.guildId, true)
                            guildManager!!.scheduler.playlistSizeCache = playlist.tracks.size - 1
                            tracks.forEach(
                                Consumer { track: AudioTrack ->

                                    if (config.maxSongDuration > 0 && track.duration > config.maxSongDuration) return@Consumer
                                    if (config.blockedSongs.contains(track.info.uri)) return@Consumer
                                    if (track is SpotifyAudioTrack && track.explicit && config.explicitFilter) return@Consumer

                                    guildManager.scheduler.queue(track.makeClone())
                                },
                            )
                        }
                    }
                }

                override fun noMatches() {
                    if (event.isAcknowledged) return
                    event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_song_found"))
                        .setEphemeral(true).queue()
                }

                override fun loadFailed(exception: FriendlyException) {
                    exception.printStackTrace()

                    val error = Error(
                        "Error al reproducir una canción",
                        Severity.ERROR,
                        exception,
                    )

                    error.push()
                    RotatingKeys.rotate(false)

                    if (event.isAcknowledged) return

                    try {
                        event.reply(formatEmoji(Constants.Emojis.error) + "Ha ocurrido un error! Comprueba que tengo los permisos necesarios para usar este comando.\nSi el problema persiste, únete a nuestro servidor de discord con el comando `/soporte` y reportanos el error\nEl código que se le ha asignado a tu error es: `${error.code}`")
                            .setEphemeral(true).queue({ }, { })
                    } catch (ex: Exception) {
                        //ignore exception
                    }
                }
            },
        )
    }

    fun loadAndPlay(
        manager: GuildMusicManager,
        event: ButtonInteraction,
        query: String,
        isSearch: Boolean,
        config: database.schema.Guild,
        lang: LangUtils
    ) {

        playerManager.loadItemOrdered(
            manager,
            query,
            object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {

                if(config.maxSongDuration > 0 && track.duration > config.maxSongDuration) {
                    event.reply("${Constants.Emojis.warning}  ${lang.get("manager.max_duration")}  (**${config.maxSongDuration.milliseconds}**)").setEphemeral(true).queue()
                    return
                }

                if(config.blockedSongs.contains(track.info.uri)) {
                    event.reply("${Constants.Emojis.warning}  ${lang.get("manager.blocked_song")}").setEphemeral(true).queue()
                    return
                }

                if(track is SpotifyAudioTrack && track.explicit && config.explicitFilter) {
                    event.reply("${Constants.Emojis.warning}  ${lang.get("manager.explicit_song")}").setEphemeral(true).queue()
                    return
                }

                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setDescription(
                        lang.get("manager.song_added")
                            .replace("%s%", StringFormat.format(track.info.title))
                            .replace("%l%", "")
                            .replace("%t%", PlayerTimestamp.get(track.duration))
                    )

                track.userData = event.member
                manager.scheduler.queue(track)
                manager.channel = event.channel as GuildMessageChannel

                val (_, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
                session?.listeners?.forEach { listener ->
                    val guildManager = getMusicManager(listener.guildId, true)
                    guildManager!!.scheduler.queue(track.makeClone())
                }

                event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {

                var firstTrack = playlist.selectedTrack
                val tracks = playlist.tracks

                if (firstTrack == null) {
                    firstTrack = playlist.tracks[0]
                }

                if (isSearch) {

                    if(config.maxSongDuration > 0 && firstTrack.duration > config.maxSongDuration) {
                        event.reply("${Constants.Emojis.warning}  ${lang.get("manager.max_duration")}  (**${config.maxSongDuration.milliseconds}**)").setEphemeral(true).queue()
                        return
                    }

                    if(config.blockedSongs.contains(firstTrack.info.uri)) {
                        event.reply("${Constants.Emojis.warning}  ${lang.get("manager.blocked_song")}").setEphemeral(true).queue()
                        return
                    }

                    if(firstTrack is SpotifyAudioTrack && firstTrack.explicit && config.explicitFilter) {
                        event.reply("${Constants.Emojis.warning}  ${lang.get("manager.explicit_song")}").setEphemeral(true).queue()
                        return
                    }

                    val embed = EmbedBuilder()
                        .setColor(config.embedColor)
                        .setDescription(
                            lang.get("manager.song_added")
                                .replace("%s%", StringFormat.format(firstTrack.info.title))
                                .replace("%l%", "")
                                .replace("%t%", PlayerTimestamp.get(firstTrack.duration))
                        )

                    firstTrack.userData = event.member
                    manager.scheduler.queue(firstTrack)
                    manager.channel = event.channel as GuildMessageChannel

                    val (_, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
                    session?.listeners?.forEach { listener ->
                        val guildManager = getMusicManager(listener.guildId, true)
                        guildManager!!.scheduler.queue(firstTrack.makeClone())
                    }

                    event.replyEmbeds(embed.build()).setEphemeral(true).queue()

                } else {

                    if (
                        !Constants.SpotifyRegex.matches(query) &&
                        !Constants.SoundCloudRegex.matches(query) &&
                        !Constants.TwitchRegex.matches(query) &&
                        !Constants.DeezerRegex.matches(query) &&
                        !Constants.AppleMusicRegex.matches(query)
                    ) {
                        event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.youtube_link"))
                            .setEphemeral(config.silent)
                            .queue()
                        return
                    }

                    if (!config.premiumEnabled && (manager.scheduler.queue.size + playlist.tracks.size > MAX_QUEUE_SIZE)) {
                        event.reply("${Constants.Emojis.warning}  ${lang.get("manager.max_queue_size").replace("%s%", "$MAX_QUEUE_SIZE")}").setEphemeral(true).queue()
                        return
                    }

                    val embed: EmbedBuilder = EmbedBuilder()
                        .setColor(config.embedColor)
                        .setDescription(
                            lang.get("manager.playlist_added").replace("%s%", playlist.tracks.filter {
                                !(config.maxSongDuration > 0 && it.duration > config.maxSongDuration) && !config.blockedSongs.contains(it.info.uri)
                            }.size.toString())
                        )

                    event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()

                    manager.scheduler.playlistSizeCache = playlist.tracks.size - 1

                    tracks.forEach(Consumer { track: AudioTrack ->

                        if(config.maxSongDuration > 0 && track.duration > config.maxSongDuration) return@Consumer
                        if(config.blockedSongs.contains(track.info.uri)) return@Consumer
                        if(track is SpotifyAudioTrack && track.explicit && config.explicitFilter) return@Consumer

                        track.userData = event.member
                        manager.scheduler.queue(track)
                    })

                    val (_, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
                    session?.listeners?.forEach { listener ->
                        val guildManager = getMusicManager(listener.guildId, true)
                        guildManager!!.scheduler.playlistSizeCache = playlist.tracks.size - 1
                        tracks.forEach(Consumer { track: AudioTrack ->

                            if(config.maxSongDuration > 0 && track.duration > config.maxSongDuration) return@Consumer
                            if(config.blockedSongs.contains(track.info.uri)) return@Consumer
                            if(track is SpotifyAudioTrack && track.explicit && config.explicitFilter) return@Consumer

                            guildManager.scheduler.queue(track.makeClone())
                        })
                    }
                }
            }

            override fun noMatches() {
                if (event.isAcknowledged) return
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_song_found"))
                    .setEphemeral(true).queue()
            }

            override fun loadFailed(exception: FriendlyException) {

                val error = Error(
                    "Error al reproducir una canción",
                    Severity.ERROR,
                    exception,
                )

                if (exception.message?.contains(Regex("5\\d\\d")) == false)
                    error.push()

                if (event.isAcknowledged) return

                try {
                    event.reply(formatEmoji(Constants.Emojis.error) + "Ha ocurrido un error! Comprueba que tengo los permisos necesarios para usar este comando.\nSi el problema persiste, únete a nuestro servidor de discord con el comando `/soporte` y reportanos el error\nEl código que se le ha asignado a tu error es: `${error.code}`")
                        .setEphemeral(true).queue({ }, { })
                } catch (ex: Exception) {
                    //ignore exception
                }
            }
        })
    }

    fun loadAndPlayBackup(
        manager: GuildMusicManager,
        currentTrackUri: String,
        currentTrackPosition: Long,
        queue: List<String>,
        member: Member,
        channel: GuildMessageChannel
    ) {
        playerManager.loadItemOrdered(manager, currentTrackUri, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                track.userData = member
                manager.scheduler.queue(track)
                manager.channel = channel

                manager.player.seekTo(currentTrackPosition)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {

                var firstTrack = playlist.selectedTrack

                if (firstTrack == null)
                    firstTrack = playlist.tracks[0]

                firstTrack.userData = member
                manager.scheduler.queue(firstTrack)
                manager.channel = channel

                manager.player.seekTo(currentTrackPosition)
            }

            override fun noMatches() {}
            override fun loadFailed(exception: FriendlyException) {}
        })

        queue.forEach { trackUri ->
            playerManager.loadItemOrdered(manager, trackUri, object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    track.userData = member
                    manager.scheduler.queue(track)
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {
                    playlist.tracks.forEach { track ->
                        track.userData = member
                        manager.scheduler.queue(track)
                    }
                }

                override fun noMatches() {}
                override fun loadFailed(exception: FriendlyException) {}
            })
        }
    }

    fun loadAndPlayBackup(
        manager: GuildMusicManager,
        queue: List<AudioTrack?>,
        queueData: List<JSONObject>,
        currentTrack: AudioTrack?,
        currentTrackData: JSONObject,
        currentTrackPosition: Long,
        member: Member,
        channel: GuildMessageChannel
    ) {

        if (currentTrack != null) {
            currentTrack.userData = member
            manager.scheduler.queue(currentTrack)
            manager.channel = channel

            manager.player.seekTo(currentTrackPosition)
        } else {
            val currentTrackUri = currentTrackData.getString("uri")

            playerManager.loadItemOrdered(manager, currentTrackUri, object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    track.userData = member
                    manager.scheduler.queue(track)
                    manager.channel = channel

                    manager.player.seekTo(currentTrackPosition)
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {

                    var firstTrack = playlist.selectedTrack

                    if (firstTrack == null)
                        firstTrack = playlist.tracks[0]

                    firstTrack.userData = member
                    manager.player.playTrack(firstTrack)
                    manager.channel = channel

                    manager.player.seekTo(currentTrackPosition)
                }

                override fun noMatches() {}
                override fun loadFailed(exception: FriendlyException) {}
            })
        }

        queue.forEachIndexed { i, track ->

            if (track != null) {
                track.userData = member
                manager.scheduler.queue(track)
            } else {
                val trackUri = queueData[i].getString("uri")

                playerManager.loadItemOrdered(manager, trackUri, object : AudioLoadResultHandler {
                    override fun trackLoaded(track: AudioTrack) {
                        track.userData = member
                        manager.scheduler.queue(track)
                    }

                    override fun playlistLoaded(playlist: AudioPlaylist) {
                        playlist.tracks.forEach { track ->
                            track.userData = member
                            manager.scheduler.queue(track)
                        }
                    }

                    override fun noMatches() {}
                    override fun loadFailed(exception: FriendlyException) {}
                })

            }
        }
    }

    fun getMusicManager(guild: Guild, channel: GuildMessageChannel?, create: Boolean = false): GuildMusicManager? {
        return getMusicManager(guild.id, channel, create)
    }

    fun getMusicManager(guildId: String, channelId: String, create: Boolean = false): GuildMusicManager? {
        val channel = try {
            Main.shards?.getTextChannelById(channelId)
        } catch (ex: Exception) {
            null
        }
        return getMusicManager(guildId, channel, create)
    }

    fun getMusicManager(guildId: String, create: Boolean = false): GuildMusicManager? {
        return getMusicManager(guildId, null, create)
    }

    fun getMusicManager(guild: Guild, create: Boolean = false): GuildMusicManager? {
        return getMusicManager(guild.id, null, create)
    }

    fun getMusicManager(guildId: String, channel: GuildMessageChannel?, create: Boolean = false): GuildMusicManager? {

        val mng = musicManagers[guildId]
        return if (mng == null && create) {
            val link = nodeManager.getLink(guildId)
            val manager = GuildMusicManager(link, channel)

            manager.player.filters.setVolume(DEFAULT_VOLUME)
            manager.player.filters.commit()

            musicManagers[guildId] = manager

            manager
        } else
            mng
    }

    fun destroyMusicManager(guild: Guild) {
        val guildId: String = guild.id
        musicManagers.remove(guildId)

        val (isHost, session) = Session.get(guild.id, guild.jda.selfUser.name)
        if(session != null && isHost)
            session.delete()
    }

    fun destroyMusicManager(guildID: String) {
        musicManagers.remove(guildID)
        val (isHost, session) = Session.get(guildID, Main.shards!!.getShardById(0)!!.selfUser.name)
        if(session != null && isHost)
            session.delete()
    }

    @Suppress("unused")
    fun destroyMusicManager(managers: List<String>) {
        musicManagers.keys.removeAll(managers.toSet())
        managers.forEach { guildID ->
            val (isHost, session) = Session.get(guildID, Main.shards!!.getShardById(0)!!.selfUser.name)
            if(session != null && isHost)
                session.delete()
        }
    }
}