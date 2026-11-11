/*
 * File:          TrackScheduler.kt
 * Path:          v3\src\main\kotlin\player\TrackScheduler.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-06-29 21:43:39 +02:00
 *
 * SHA-256: 4b02cd0e1f767afe36e22457156895070b85fb47fe57cbfe494b4533528cac3f
 *
 */


package player

import atenea.driver.player.AteneaPlayer
import atenea.driver.player.IPlayer
import atenea.driver.player.event.PlayerEventListenerAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import database.schema.Guild
import database.schema.Songs
import enums.AudioFilters
import enums.LoopMode
import functions.StringFormat
import langs.LangUtils
import metrics.DailyStats
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.slf4j.LoggerFactory
import player.Player.getMusicManager
import player.spotify.SpotifyAudioTrack
import thirdparty.spotify.SpotifyApi
import utils.Constants
import utils.PlayerTimestamp
import java.util.*
import java.util.Collections.shuffle

class TrackScheduler(val player: AteneaPlayer) : PlayerEventListenerAdapter() {

    var isRepeating: LoopMode = LoopMode.NONE
    val queue: Queue<AudioTrack> = LinkedList()
    var lastTrack: AudioTrack? = null
    private var currentTrack: AudioTrack? = null
    val filters: MutableList<AudioFilters> = mutableListOf()

    private var lastMessage: Message? = null
    var lastChannel: GuildMessageChannel? = null
    private var lastConfig: Guild? = null

    var playlistSizeCache = 0

    fun queue(track: AudioTrack?) {
        if (player.playingTrack == null) {
            player.playTrack(track)
        } else {
            queue.offer(track)
        }
    }

    fun skip(channel: GuildMessageChannel?, force: Boolean = false) {
        val track: AudioTrack? = queue.poll()
        val guildId = channel?.guild?.id ?: (lastTrack?.userData as Member?)?.guild?.id ?: (currentTrack?.userData as Member?)?.guild?.id ?: return

        val guild = if (lastConfig != null) lastConfig!! else Guild.get(guildId)
        lastConfig = guild

        if (track == null) {
            if (isRepeating == LoopMode.NONE && !guild.autoplay || force) {
                player.stopTrack()
                onTrackEnd(player, lastTrack ?: currentTrack!!, AudioTrackEndReason.STOPPED)

                if(guild.premiumEnabled && guild.radioEnabled && guild.radioChannelID.isNotBlank())
                    return

                val manager = getMusicManager(guildId, true)!!
                try {
                    manager.link.destroy()
                } catch (_: Exception) {
                    // linker already destroyed
                }
                Player.destroyMusicManager(guildId)

            }
            return
        }

        player.playTrack(track.makeClone())
        val member: Member = track.userData as Member
        val mng = getMusicManager(member.guild.id, true)!!

        if (isRepeating == LoopMode.QUEUE && lastTrack != null) {
            queue.offer(lastTrack!!.makeClone())
        }

        if (channel != null) {
            mng.channel = channel
            lastChannel = channel
        }
    }

    override fun onTrackEnd(player: IPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        lastTrack = track

        val member: Member = lastTrack?.userData as Member? ?: currentTrack?.userData as Member? ?: return
        val guild = if (lastConfig != null) lastConfig!! else Guild.get(member.guild.id)
        lastConfig = guild

        if (lastMessage != null) {

            lastMessage = try {
                if(guild.silent)
                    lastMessage!!.delete().queue({}, {})
                else
                    lastMessage!!.editMessageComponents().queue({}, {})
                null
            } catch (e: Exception) {
                null
            }
        }

        if (endReason.mayStartNext) {
            val mng: GuildMusicManager = getMusicManager(member.guild.id) ?: return

            if (mng.channel != null)
                lastChannel = mng.channel

            when (isRepeating) {
                LoopMode.TRACK -> player.playTrack(track.makeClone())
                else -> {

                    if (guild.autoplay && queue.isEmpty() && isRepeating == LoopMode.NONE) {
                        val artist = SpotifyApi.searchForArtist(lastTrack!!.info.author) ?: return
                        val tracks = SpotifyApi.getRecommendations(track, artist)

                        if (tracks.isNotEmpty()) {

                            val firstTrack = tracks[0]
                            firstTrack.userData = lastTrack!!.userData
                            player.playTrack(firstTrack)

                            tracks.filter { it != firstTrack }.forEach { rec ->
                                rec.userData = lastTrack!!.userData
                                queue(rec)
                            }
                        }
                    } else
                        if(!queue.isEmpty() || isRepeating != LoopMode.NONE || guild.autoplay)
                            skip(null)
                }
            }
        }
    }

    override fun onTrackStart(player: IPlayer, track: AudioTrack) {
        currentTrack = track

        val size =
            (if (playlistSizeCache > 0) playlistSizeCache else queue.size) + if (isRepeating == LoopMode.QUEUE) 1 else 0
        playlistSizeCache = 0

        var explicit = false

        val image =
            if (track.sourceManager.sourceName == "spotify") {
                try {
                    val newTrack: SpotifyAudioTrack = track as SpotifyAudioTrack
                    explicit = newTrack.explicit
                    newTrack.artworkURL ?: Constants.Images.noImage
                } catch (e: Exception) {
                    Constants.Images.noImage
                }
            } else {
                when {
                    Constants.SoundCloudRegex.matches(track.info.uri) -> Constants.Images.soundCloudIcon
                    Constants.TwitchRegex.matches(track.info.uri) -> Constants.Images.twitchIcon
                    Constants.DeezerRegex.matches(track.info.uri) -> Constants.Images.deezerIcon
                    Constants.AppleMusicRegex.matches(track.info.uri) -> Constants.Images.appleMusicIcon
                    Constants.TikTokRegex.matches(track.info.uri) -> Constants.Images.tikTokIcon
                    Constants.RedditRegex.matches(track.info.uri) -> Constants.Images.redditIcon
                    else -> Constants.Images.noImage
                }
            }

        val member: Member = track.userData as Member
        val mng = getMusicManager(member.guild.id, true)!!

        val guild = if (lastConfig != null) lastConfig!! else Guild.get(member.guild.id)
        lastConfig = guild
        val lang = LangUtils(guild.language)

        Songs.get(track.info.uri).increment()
        DailyStats.incrementSongs(member.jda)

        if (System.currentTimeMillis() % 86400000 == 0L) {
            DailyStats.clear()
        }

        val duration = "${
            when {
                size == 0 && guild.autoplay -> "∞"
                size == 0 && !guild.autoplay -> "0"
                size > 0 && guild.autoplay -> "$size + ∞"
                size > 0 && !guild.autoplay -> size.toString()
                else -> size.toString()
            }
        } ${
            when {
                size == 1 && !guild.autoplay -> lang.get("commands.play.song")
                else -> lang.get("commands.play.songs")
            }
        }"

        val canUseEmojis = member.guild.selfMember.hasPermission(Permission.MESSAGE_EXT_EMOJI)

        val embed = EmbedBuilder()
            .setColor(guild.embedColor)
            .setAuthor(
                lang.get("events.trackStart.embed_author"),
                null,
                member.user.avatarUrl
                    ?: Constants.Images.noImage
            )
            .setThumbnail(image)
            .setDescription(
                "**[${StringFormat.format(track.info.title ?: "Unknown")}](${track.info.uri})** ${
                    if (explicit && canUseEmojis) Constants.Emojis.explicit else ""
                } from `${
                    if (track.info.author == null || track.info.author.toString() == "") "Unknown" else track.info.author.toString()
                }`"
            )
            .addField(lang.get("commands.play.duration"), "`${PlayerTimestamp.get(track.duration)}`", true)
            .addField(lang.get("commands.play.in_queue"), "`$duration`", true)

        if (mng.channel?.canTalk() == true && member.guild.selfMember.hasPermission(
                mng.channel!!,
                Permission.MESSAGE_EMBED_LINKS
            )
        ) {
            lastChannel = mng.channel
            mng.channel?.sendMessageEmbeds(embed.build())?.setActionRow(
                Button.success("cmd:play:pause", lang.get("commands.play.pause_resume")),
                Button.primary("cmd:play:skip", "Skip"),
                Button.danger("cmd:play:stop", lang.get("commands.play.stop")),
                Button.secondary("cmd:play:showQueue", "Queue")
            )?.queue({
                if (lastTrack != null)
                    lastTrack!!.userData = member

                lastMessage = it
            }, {})
        } else {
            if (lastChannel?.canTalk() == true && member.guild.selfMember.hasPermission(
                    lastChannel!!,
                    Permission.MESSAGE_EMBED_LINKS
                )
            ) {
                try {
                    lastChannel?.sendMessageEmbeds(embed.build())?.setActionRow(
                        Button.success("cmd:play:pause", lang.get("commands.play.pause_resume")),
                        Button.primary("cmd:play:skip", "Skip"),
                        Button.danger("cmd:play:stop", lang.get("commands.play.stop")),
                        Button.secondary("cmd:play:showQueue", "Queue")
                    )?.queue({
                        if (lastTrack != null)
                            lastTrack!!.userData = member

                        lastMessage = it
                    }, {})
                } catch (_: Exception) {

                }
            }
        }
    }

    override fun onTrackStuck(player: IPlayer, track: AudioTrack?, thresholdMs: Long) {
        LoggerFactory.getLogger(TrackScheduler::class.java).error("Track stuck: ${track?.info?.title} - ${track?.info?.uri}")
    }

    override fun onTrackException(player: IPlayer?, track: AudioTrack?, exception: java.lang.Exception?) {
        LoggerFactory.getLogger(TrackScheduler::class.java).error("Track exception: ${track?.info?.title} - ${track?.info?.uri}: ${exception?.message}")
        //exception?.printStackTrace()
    }

    fun shuffle() {
        shuffle(queue as List<*>)
    }

    fun shuffle(rand: Random) {
        shuffle(queue as List<*>, rand)
    }
}