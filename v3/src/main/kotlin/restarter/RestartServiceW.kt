/*
 * File:          RestartServiceW.kt
 * Path:          v3\src\main\kotlin\restarter\RestartServiceW.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-07 01:11:56 +02:00
 *
 * SHA-256: ab96a85fe19c65746b05db7280f40301e4717202d2c3554faba5563051b00d73
 *
 */


package restarter

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.sharding.ShardManager
import org.json.JSONArray
import org.json.JSONObject
import player.GuildMusicManager
import player.Player
import player.spotify.SpotifyAudioTrack
import java.io.File

class RestartServiceW(
    shardingManager: ShardManager,
) {

    private val shards: ShardManager
    private val musicManagers: Map<String, GuildMusicManager>

    private val playingPlayersFile: File
    init {
        this.shards = shardingManager
        this.musicManagers = Player.musicManagers

        File("backup").mkdirs()
        this.playingPlayersFile = File("backup/${this.shards.getShardById(0)!!.selfUser.name.replace(Regex("\\s+"),"")}_playing_players_dump.json")
    }

    fun savePlayingPlayers() {
        val playingPlayers = musicManagers.filter { it.value.player.playingTrack != null }
        val jsonDump = JSONArray()

        playingPlayers.forEach { (guildId, musicManager) ->

            val queue = JSONArray(musicManager.scheduler.queue.map {
                JSONObject()
                    .put("name", it.info.title)
                    .put("artists", it.info.author)
                    .put("duration_ms", it.info.length)
                    .put("id", it.identifier)
                    .put("uri", it.info.uri)
                    .put("source_manager", it.sourceManager.sourceName)
                    .put("image", if (it is SpotifyAudioTrack) it.artworkURL else "")
                    .put("isrc", if (it is SpotifyAudioTrack) it.isrc else "")
                    .put("explicit", if (it is SpotifyAudioTrack) it.explicit else false)
            })

            val track = musicManager.player.playingTrack ?: return@forEach
            val dumpInfo = JSONObject()
            dumpInfo.put("guild_id", guildId)
            dumpInfo.put("text_channel_id", musicManager.channel?.id)
            dumpInfo.put("voice_channel_id", (track.userData as Member).voiceState?.channel?.id ?: return@forEach)
            dumpInfo.put("queue", queue)
            dumpInfo.put("current_track", JSONObject().apply {
                put("uri", track.info.uri)
                put("name", track.info.title)
                put("artists", track.info.author)
                put("duration_ms", track.info.length)
                put("id", track.identifier)
                put("position", musicManager.player.trackPosition)
                put("source_manager", track.sourceManager.sourceName)
                put("image", if (track is SpotifyAudioTrack) track.artworkURL else "")
                put("isrc", if (track is SpotifyAudioTrack) track.isrc else "")
                put("explicit", if (track is SpotifyAudioTrack) track.explicit else false)
            })
            dumpInfo.put("volume", musicManager.player.filters.volume * 100)
            dumpInfo.put("is_repeating", musicManager.scheduler.isRepeating.name)

            jsonDump.put(dumpInfo)

        }

        playingPlayersFile.writeText(jsonDump.toString(4))

    }
}