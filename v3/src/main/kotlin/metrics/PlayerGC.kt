/*
 * File:          PlayerGC.kt
 * Path:          v3\src\main\kotlin\metrics\PlayerGC.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-15 15:11:06 +01:00
 *
 * SHA-256: b3e67b9123bea97a45073a3bc72a14468ce97c158267cc1918cff78abf613c2e
 *
 */


package metrics

import io.prometheus.client.Collector
import io.prometheus.client.Gauge

object PlayerGC {

    private val GC_PASS = Gauge.build()
        .name("kenabot_player_gc")
        .help("Amount of players that have been garbage collected")
        .labelNames("bot_name")
        .create()

    fun register() {
        GC_PASS.register<Collector>()
    }
    fun update(botName: String, amount: Int) {
        GC_PASS.labels(botName).set(amount.toDouble())
    }

}