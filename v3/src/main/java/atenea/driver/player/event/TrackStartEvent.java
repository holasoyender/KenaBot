/*
 * File:          TrackStartEvent.java
 * Path:          v3\src\main\java\atenea\driver\player\event\TrackStartEvent.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-07 01:11:56 +02:00
 *
 * SHA-256: 496deb012130e4c2bb3fd23d906355f351eda7318d135bd2b8caccb5bea7f933
 *
 */


package atenea.driver.player.event;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import atenea.driver.player.IPlayer;

public class TrackStartEvent extends PlayerEvent {

    private final AudioTrack track;

    public TrackStartEvent(IPlayer player, AudioTrack track) {
        super(player);
        this.track = track;
    }

    public AudioTrack getTrack() {
        return track;
    }
}
