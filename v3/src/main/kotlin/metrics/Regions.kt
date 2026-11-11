/*
 * File:          Regions.kt
 * Path:          v3\src\main\kotlin\metrics\Regions.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-14 18:13:13 +02:00
 *
 * SHA-256: 40c28731596aeb4e8176fd134f29cb2d351fa35a40e30423224272fd547e53b1
 *
 */


package metrics

import io.prometheus.client.Collector
import io.prometheus.client.Gauge
import org.json.JSONObject

object Regions {

    private val cache: MutableList<Pair<String, Int>> = mutableListOf()

    private val REGIONS = Gauge.build()
        .name("kenabot_voice_regions")
        .help("Join per region of the bot")
        .labelNames("region")
        .create()

    fun onMessage(message: JSONObject) {

        val region = message.getString("endpoint").split(":").getOrNull(0)?.replace("[0-9]".toRegex(), "")?.split(".")
            ?.getOrNull(0) ?: return

        val index = cache.indexOfFirst { it.first == region }
        if (index != -1) {
            cache[index] = Pair(cache[index].first, cache[index].second + 1)
        } else {
            cache.add(Pair(region, 1))
        }
    }

    fun register() {
        REGIONS.register<Collector>()
    }

    fun update() {

        if(cache.size > 0) {
            database.schema.Regions.updateFromCache(cache)
            cache.clear()
        }

        val top10regions = database.schema.Regions.getTop10()

        top10regions.forEach {
            REGIONS.labels(it.region).set((it.count).toDouble())
        }
    }
}