/*
 * File:          TrackStuckEvent.java
 * Path:          v3\src\main\java\atenea\driver\player\event\TrackStuckEvent.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-07 01:11:56 +02:00
 *
 * SHA-256: 258c80f2f688dd6f887bbba82b1b542dce9e3270e7e24c09f6db348186151ac5
 *
 */


package atenea.driver.player.event;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import atenea.driver.player.IPlayer;

public class TrackStuckEvent extends PlayerEvent {

    private final AudioTrack track;
    private final long thresholdMs;

    public TrackStuckEvent(IPlayer player, AudioTrack track, long thresholdMs) {
        super(player);
        this.track = track;
        this.thresholdMs = thresholdMs;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public long getThresholdMs() {
        return thresholdMs;
    }
}
