/*
 * File:          TrackEndEvent.java
 * Path:          v3\src\main\java\atenea\driver\player\event\TrackEndEvent.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-07 01:11:56 +02:00
 *
 * SHA-256: 8214a356ae0cdd157d436771e545cd4cf3089ba1d6dce43bee71af9b9cb717c4
 *
 */


package atenea.driver.player.event;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import atenea.driver.player.IPlayer;

public class TrackEndEvent extends PlayerEvent {

    private final AudioTrack track;
    private final AudioTrackEndReason reason;

    public TrackEndEvent(IPlayer player, AudioTrack track, AudioTrackEndReason reason) {
        super(player);
        this.track = track;
        this.reason = reason;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public AudioTrackEndReason getReason() {
        return reason;
    }
}
