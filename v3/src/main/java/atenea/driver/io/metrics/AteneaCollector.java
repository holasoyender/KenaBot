/*
 * File:          AteneaCollector.java
 * Path:          v3\src\main\java\atenea\driver\io\metrics\AteneaCollector.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: 800e33a7ad49c13c29b5fbed784fb4257abee883bf723b89640e065573632ff9
 *
 */


package atenea.driver.io.metrics;

import com.mongodb.lang.NonNull;
import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import atenea.driver.io.Atenea;
import atenea.driver.io.AteneaSocket;
import atenea.driver.io.RemoteStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class AteneaCollector extends Collector {

    private final Atenea<?> atenea;

    public AteneaCollector(@NonNull Atenea<?> ateneaInstance) {
        this.atenea = ateneaInstance;
    }

    @Override
    public List<MetricFamilySamples> collect() {

        List<MetricFamilySamples> mfs = new ArrayList<>();
        List<String> labelNames = Collections.singletonList("node");


        GaugeMetricFamily players = new GaugeMetricFamily("atenea_players_current",
                "Amount of players", labelNames);
        mfs.add(players);
        GaugeMetricFamily playingPlayers = new GaugeMetricFamily("atenea_playing_players_current",
                "Amount of playing players", labelNames);
        mfs.add(playingPlayers);
        GaugeMetricFamily uptimeSeconds = new GaugeMetricFamily("atenea_uptime_seconds",
                "Uptime of the node", labelNames);
        mfs.add(uptimeSeconds);


        GaugeMetricFamily memFree = new GaugeMetricFamily("atenea_mem_free_bytes",
                "Amount of free memory", labelNames);
        mfs.add(memFree);
        GaugeMetricFamily memUsed = new GaugeMetricFamily("atenea_mem_used_bytes",
                "Amount of used memory", labelNames);
        mfs.add(memUsed);
        GaugeMetricFamily memAllocated = new GaugeMetricFamily("atenea_mem_allocated_bytes",
                "Amount of allocated memory", labelNames);
        mfs.add(memAllocated);
        GaugeMetricFamily memReservable = new GaugeMetricFamily("atenea_mem_reservable_bytes",
                "Amount of reservable memory", labelNames);
        mfs.add(memReservable);

        GaugeMetricFamily cpuCores = new GaugeMetricFamily("atenea_cpu_cores",
                "Amount of cpu cores", labelNames);
        mfs.add(cpuCores);
        GaugeMetricFamily systemLoad = new GaugeMetricFamily("atenea_load_system",
                "Total load of the system", labelNames);
        mfs.add(systemLoad);
        GaugeMetricFamily ateneaLoad = new GaugeMetricFamily("atenea_load_atenea",
                "Load caused by Atenea", labelNames);
        mfs.add(ateneaLoad);


        GaugeMetricFamily averageFramesSentPerMinute = new GaugeMetricFamily("atenea_average_frames_sent_per_minute",
                "Average frames sent per minute", labelNames);
        mfs.add(averageFramesSentPerMinute);
        GaugeMetricFamily averageFramesNulledPerMinute = new GaugeMetricFamily("atenea_average_frames_nulled_per_minute",
                "Average frames nulled per minute", labelNames);
        mfs.add(averageFramesNulledPerMinute);
        GaugeMetricFamily averageFramesDeficitPerMinute = new GaugeMetricFamily("atenea_average_frames_deficit_per_minute",
                "Average frames deficit per minute", labelNames);
        mfs.add(averageFramesDeficitPerMinute);


        List<AteneaSocket> nodes = atenea.getNodes();
        for (AteneaSocket node : nodes) {
            List<String> labels = Collections.singletonList(node.getName());
            RemoteStats stats = node.getStats();
            if (stats == null) {
                continue;
            }

            players.addMetric(labels, stats.getPlayers());
            playingPlayers.addMetric(labels, stats.getPlayingPlayers());
            uptimeSeconds.addMetric(labels, (double) stats.getUptime() / 1000);

            memFree.addMetric(labels, stats.getMemFree());
            memUsed.addMetric(labels, stats.getMemUsed());
            memAllocated.addMetric(labels, stats.getMemAllocated());
            memReservable.addMetric(labels, stats.getMemReservable());

            cpuCores.addMetric(labels, stats.getCpuCores());
            systemLoad.addMetric(labels, stats.getSystemLoad());
            ateneaLoad.addMetric(labels, stats.getAteneaLoad());

            averageFramesSentPerMinute.addMetric(labels, stats.getAvgFramesSentPerMinute());
            averageFramesNulledPerMinute.addMetric(labels, stats.getAvgFramesNulledPerMinute());
            averageFramesDeficitPerMinute.addMetric(labels, stats.getAvgFramesDeficitPerMinute());
        }

        return mfs;
    }
}
