/*
 * File:          GcNotificationListener.kt
 * Path:          v3\src\main\kotlin\metrics\GcNotificationListener.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 16:20:00 +02:00
 *
 * SHA-256: 24774d497ecf3bdd0fd7a93f5ad6341980ab3e3606f980812f50249e78b6366f
 *
 */


package metrics

import com.sun.management.GarbageCollectionNotificationInfo
import com.sun.management.GcInfo
import io.prometheus.client.Collector
import io.prometheus.client.Histogram
import javax.management.Notification
import javax.management.NotificationListener
import javax.management.openmbean.CompositeData
import com.sun.management.GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION
import com.sun.management.GarbageCollectionNotificationInfo.from

class GcNotificationListener : NotificationListener {
    private val gcPauses: Histogram = Histogram.build()
        .name("kenabot_gc_pauses_seconds")
        .help("Garbage collection pauses by buckets")
        .buckets(0.025, 0.050, 0.100, 0.200, 0.400, 0.800, 1.600)
        .register()

    override fun handleNotification(notification:  Notification, handback: Any?) {
        if (GARBAGE_COLLECTION_NOTIFICATION == notification.type) {
            val notificationInfo: GarbageCollectionNotificationInfo = from(notification.userData as CompositeData)
            val info: GcInfo = notificationInfo.gcInfo
            if ("No GC" != notificationInfo.gcCause) {
                gcPauses.observe(info.duration / Collector.MILLISECONDS_PER_SECOND)
            }
        }
    }
}