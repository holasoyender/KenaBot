/*
 * File:          TrackExceptionEvent.java
 * Path:          v3\src\main\java\atenea\driver\player\event\TrackExceptionEvent.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-07 01:11:56 +02:00
 *
 * SHA-256: 7d46317dd80c2a4f657c6f1808e5f21b1114559f5ea9ea15de54f4d968a5e1e6
 *
 */


package atenea.driver.player.event;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import atenea.driver.player.IPlayer;

public class TrackExceptionEvent extends PlayerEvent {

    private final AudioTrack track;
    private final Exception exception;

    public TrackExceptionEvent(IPlayer player, AudioTrack track, Exception exception) {
        super(player);
        this.track = track;
        this.exception = exception;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public Exception getException() {
        return exception;
    }
}
