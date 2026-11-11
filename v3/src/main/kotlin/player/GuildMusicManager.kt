/*
 * File:          GuildMusicManager.kt
 * Path:          v3\src\main\kotlin\player\GuildMusicManager.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-29 12:08:20 +02:00
 *
 * SHA-256: 9bf4869f0c12c4d97ea418c8f29fc88e2ef99e5562f36702892ee7d8feb90878
 *
 */


package player

import atenea.NodeManager
import atenea.driver.io.jda.JdaLink
import atenea.driver.player.AteneaPlayer
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import org.slf4j.LoggerFactory

@Suppress("LocalVariableName")
class GuildMusicManager(var link: JdaLink, _channel: GuildMessageChannel?) {

    private val logger = LoggerFactory.getLogger(GuildMusicManager::class.java)

    var player: AteneaPlayer
    var scheduler: TrackScheduler
    var channel: GuildMessageChannel? = null

    init {
        player = link.player
        scheduler = TrackScheduler(player)
        player.addListener(scheduler)
        channel = _channel
    }


    fun recreate() {
        logger.warn("Forcing recreation of GuildMusicManager for ${link.guildId} (shard ${link.jda.shardInfo.shardId})")

        this.link = NodeManager.nodeManager.getLink(link.guildId) as JdaLink
        player = link.player
        scheduler = TrackScheduler(player)
        player.addListener(scheduler)
    }

}