/*
 * File:          AteneaInternalPlayerEventHandler.java
 * Path:          v3\src\main\java\atenea\driver\player\AteneaInternalPlayerEventHandler.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: e1c890fa446c217468967ef1fd34b38f9e55224cd7c867db32910d1a3109c4be
 *
 */


package atenea.driver.player;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import atenea.driver.player.event.PlayerEventListenerAdapter;

class AteneaInternalPlayerEventHandler extends PlayerEventListenerAdapter {
    @Override
    public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason != AudioTrackEndReason.REPLACED && endReason != AudioTrackEndReason.STOPPED) {
            ((AteneaPlayer) player).clearTrack();
        }
    }
}
