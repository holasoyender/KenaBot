/*
 * File:          DiscordLatency.kt
 * Path:          v3\src\main\kotlin\metrics\DiscordLatency.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 16:20:00 +02:00
 *
 * SHA-256: 0d9465c815fde51c2b4ab8ca20ccc51b0cd18a668e27542272c78037957ae9c2
 *
 */


package metrics

import io.prometheus.client.Collector
import io.prometheus.client.Gauge
import net.dv8tion.jda.api.sharding.ShardManager

object DiscordLatency {

    private val GATEWAY_LATENCY = Gauge.build()
        .name("kenabot_shard_latency")
        .help("Gateway latency in seconds, per shard")
        .labelNames("shard", "bot_name")
        .create()

    private val REST_LATENCY = Gauge.build()
        .name("kenabot_rest_latency")
        .help("Rest latency in seconds")
        .labelNames("bot_name")
        .create()

    fun register() {
        GATEWAY_LATENCY.register<Collector>()
        REST_LATENCY.register<Collector>()
    }
    fun update(shardManager: ShardManager) {
        shardManager.shards.forEach {
            val ping = it.gatewayPing
            if (ping >= 0) {
                GATEWAY_LATENCY.labels(it.shardInfo.shardId.toString(), shardManager.getShardById(0)!!.selfUser.name)
                    .set(ping / 1.0)
            }
        }
        shardManager.shardCache.iterator().next().restPing.queue { ping -> REST_LATENCY.labels(shardManager.getShardById(0)!!.selfUser.name).set(ping / 1.0) }
    }
}