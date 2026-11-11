/*
 * File:          RemoteStats.java
 * Path:          v3\src\main\java\atenea\driver\io\RemoteStats.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: 01318f652b2b5fb1f33c53f17b40728a03b1f7773ff98ec7091a39a3ffa3d6ad
 *
 */


package atenea.driver.io;

import org.json.JSONObject;

@SuppressWarnings({"unused", "WeakerAccess"})
public class RemoteStats {

    private final JSONObject json;
    private final int players;
    private final int playingPlayers;
    private final long uptime; //in millis

    // In bytes
    private final long memFree;
    private final long memUsed;
    private final long memAllocated;
    private final long memReservable;

    private final int cpuCores;
    private final double systemLoad;
    private final double ateneaLoad;

    private int avgFramesSentPerMinute = -1;
    private int avgFramesNulledPerMinute = -1;
    private int avgFramesDeficitPerMinute = -1;

    RemoteStats(JSONObject json) {
        this.json = json;
        players = json.getInt("players");
        playingPlayers = json.getInt("playingPlayers");
        uptime = json.getLong("uptime");

        memFree = json.getJSONObject("memory").getLong("free");
        memUsed = json.getJSONObject("memory").getLong("used");
        memAllocated = json.getJSONObject("memory").getLong("allocated");
        memReservable = json.getJSONObject("memory").getLong("reservable");

        cpuCores = json.getJSONObject("cpu").getInt("cores");
        systemLoad = json.getJSONObject("cpu").getDouble("systemLoad");
        ateneaLoad = json.getJSONObject("cpu").getDouble("ateneaLoad");

        JSONObject frames = json.optJSONObject("frameStats");

        if (frames != null) {
            avgFramesSentPerMinute = frames.getInt("sent");
            avgFramesNulledPerMinute = frames.getInt("nulled");
            avgFramesDeficitPerMinute = frames.getInt("deficit");
        }
    }

    public JSONObject getAsJson() {
        return json;
    }

    public int getPlayers() {
        return players;
    }

    public int getPlayingPlayers() {
        return playingPlayers;
    }

    public long getUptime() { //in millis
        return uptime;
    }

    public long getMemFree() {
        return memFree;
    }

    public long getMemUsed() {
        return memUsed;
    }

    public long getMemAllocated() {
        return memAllocated;
    }

    public long getMemReservable() {
        return memReservable;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public double getSystemLoad() {
        return systemLoad;
    }

    public double getAteneaLoad() {
        return ateneaLoad;
    }

    public int getAvgFramesSentPerMinute() {
        return avgFramesSentPerMinute;
    }

    public int getAvgFramesNulledPerMinute() {
        return avgFramesNulledPerMinute;
    }

    public int getAvgFramesDeficitPerMinute() {
        return avgFramesDeficitPerMinute;
    }

    @Override
    public String toString() {
        return "RemoteStats{" +
                "players=" + players +
                ", playingPlayers=" + playingPlayers +
                ", uptime=" + uptime +
                ", memFree=" + memFree +
                ", memUsed=" + memUsed +
                ", memAllocated=" + memAllocated +
                ", memReservable=" + memReservable +
                ", cpuCores=" + cpuCores +
                ", systemLoad=" + systemLoad +
                ", ateneaLoad=" + ateneaLoad +
                ", avgFramesSentPerMinute=" + avgFramesSentPerMinute +
                ", avgFramesNulledPerMinute=" + avgFramesNulledPerMinute +
                ", avgFramesDeficitPerMinute=" + avgFramesDeficitPerMinute +
                '}';
    }
}
