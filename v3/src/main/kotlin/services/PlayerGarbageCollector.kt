/*
 * File:          PlayerGarbageCollector.kt
 * Path:          v3\src\main\kotlin\services\PlayerGarbageCollector.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-05-03 14:48:22 +02:00
 *
 * SHA-256: d0fe7262936262a7501e726ae902c4b4e69ab2f3df8564d161f4b53782d229c6
 *
 */


package services

import database.schema.Guild
import io.sentry.Sentry
import metrics.PlayerGC
import player.Player
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.thread
import kotlin.concurrent.write

object PlayerGarbageCollector {

    private val lock = ReentrantReadWriteLock()

    fun startLoop() {

        thread(isDaemon = true, name = "PlayerGarbageCollectorService") {
            while (true) {

                try {

                    lock.read {

                        val toRemove = Player.musicManagers
                            .filterValues {

                                val guildId = Player.musicManagers.keys.first { key -> Player.musicManagers[key] == it }
                                val player = it.player
                                val scheduler = it.scheduler

                                val guild = Main.shards!!.getGuildById(guildId)

                                if (guild == null) {
                                    true
                                } else {

                                    if (player.playingTrack == null && scheduler.queue.isEmpty()) {
                                        val config = Guild.get(guildId)

                                        if (!config.premiumEnabled || !config.radioEnabled) {
                                            try {
                                                it.link.destroy()
                                            } catch (e: Exception) {
                                                // already destroyed
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    } else {
                                        false
                                    }
                                }
                            }
                            .keys

                        lock.write {
                            Player.musicManagers.keys.removeAll(toRemove)
                            PlayerGC.update(Main.shards!!.getShardById(0)!!.selfUser.name, toRemove.size)
                        }
                    }

                    Thread.sleep(30000)
                } catch (e: Exception) {
                    Sentry.captureException(e)
                    //e.printStackTrace()
                    PlayerGC.update(Main.shards!!.getShardById(0)!!.selfUser.name, -1)
                    Thread.sleep(30000)
                }
            }
        }
    }
}