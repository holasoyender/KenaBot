/*
 * File:          BootServiceW.kt
 * Path:          v3\src\main\kotlin\restarter\BootServiceW.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-07 01:11:56 +02:00
 *
 * SHA-256: 280a0f4163128d20cda559f102fa781ed3ae14600e43b915f6f8c8ccad83b270
 *
 */


package restarter

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import enums.LoopMode
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.LoggerFactory
import player.Player
import player.Player.spotifySourceManager
import player.spotify.SpotifyAudioTrack
import utils.Constants
import java.io.File

class BootServiceW(
    shardId: Int,
    guildIds: Array<String>,
    shard: JDA
) {

    private val shardId: Int
    private val guildIds: Array<String>
    private val shard: JDA

    private var file: File

    private val logger = LoggerFactory.getLogger(BootServiceW::class.java)

    init {
        this.shardId = shardId
        this.guildIds = guildIds
        this.shard = shard

        this.file = File("backup/${this.shard.selfUser.name.replace(Regex("\\s+"), "")}_playing_players_dump.json")
    }

    fun resumePlayers() {

        if (!file.exists()) {
            this.file = File("${this.shard.selfUser.name.replace(Regex("\\s+"), "")}_playing_players_dump.json")
            if (!file.exists()) return
        }

        val content = file.readText()
        if (content.isEmpty()) return

        if (shardId == shard.shardManager!!.shardsTotal - 1)
            file.delete()

        val jsonDump = JSONArray(content).filter {
            (it as JSONObject).getString("guild_id") in guildIds &&
                    (GuildCache.get(it.getString("guild_id")) == null || GuildCache.get(it.getString("guild_id"))?.done == false)
        }

        if (jsonDump.isEmpty()) logger.info("No players to resume for shard $shardId") else
            logger.info("Resuming ${jsonDump.size} players on shard $shardId...")

        val resumedPlayers = jsonDump.filter { dumpInfo ->

            val info = dumpInfo as JSONObject

            val cache = GuildCache(
                info.getString("guild_id"),
                shardId,
                true
            )

            GuildCache.add(cache)

            val guildId = info.getString("guild_id")
            val textChannelId = info.getString("text_channel_id")
            val voiceChannelId = info.getString("voice_channel_id")
            val queueData = try {
                info.getJSONArray("queue").map { it as JSONObject }
            } catch (e: Exception) {
                null
            }

            val oldQueue = try {
                info.getJSONArray("queue").map { it as String }
            } catch (e: Exception) {
                listOf()
            }

            val currentTrackData = info.getJSONObject("current_track")
            val currentTrackPosition = info.getJSONObject("current_track").getLong("position")

            val volume = info.getInt("volume")
            val isRepeating = try { info.getString("is_repeating") } catch (e: Exception) {
                val isRepeating = info.getBoolean("is_repeating")
                if (isRepeating) LoopMode.TRACK.name else LoopMode.NONE.name
            }

            val guild = shard.getGuildById(guildId) ?: return@filter false
            val textChannel = guild.getTextChannelById(textChannelId) as GuildMessageChannel? ?: guild.getVoiceChannelById(textChannelId) as GuildMessageChannel? ?: guild.getStageChannelById(textChannelId) as GuildMessageChannel? ?: return@filter false
            val voiceChannel = guild.getVoiceChannelById(voiceChannelId) ?: guild.getStageChannelById(voiceChannelId) ?: return@filter false

            val manager = Player.getMusicManager(guild, textChannel, true)!!
            manager.player.filters.setVolume(volume.toFloat() / 100)
            manager.player.filters.commit()
            manager.scheduler.isRepeating = try { LoopMode.valueOf(isRepeating) } catch (e: Exception) { LoopMode.NONE }
            manager.scheduler.playlistSizeCache = queueData?.size ?: oldQueue.size

            manager.link.connect(voiceChannel)

            val queue = queueData?.map { trackData ->
                if (trackData.getString("source_manager") == "spotify") {
                    parseSpotifyTrack(trackData)
                } else
                    null
            } ?: emptyList()

            val currentTrack = try {
                if (currentTrackData.getString("source_manager") == "spotify") {
                    parseSpotifyTrack(currentTrackData)
                } else
                    null
            } catch (e: Exception) {
                null
            }

            if(queueData != null)
                Player.loadAndPlayBackup(manager, queue, queueData, currentTrack, currentTrackData, currentTrackPosition, guild.selfMember, textChannel)
            else {
                logger.info("Se ha encontrado un archivo de backup antiguo, no es recomendable usarlo.")
                Player.loadAndPlayBackup(manager, currentTrackData.getString("uri"), currentTrackPosition, oldQueue, guild.selfMember, textChannel)
            }

            true
        }

        logger.info("Resumed ${resumedPlayers.size}/${jsonDump.size} players on shard $shardId")
    }

    private fun parseSpotifyTrack(json: JSONObject): AudioTrack {
        return SpotifyAudioTrack(
            AudioTrackInfo(
                json.getString("name"),
                json.getString("artists"),
                json.getLong("duration_ms"),
                json.getString("id"),
                false,
                json.getString("uri")
            ),
            try {json.getString("isrc") } catch (e: Exception) { null },
            try { json.getString("image") } catch (e: Exception) { Constants.Images.noImage },
            try { json.getBoolean("explicit") } catch (e: Exception) { false },
            spotifySourceManager
        )
    }
}