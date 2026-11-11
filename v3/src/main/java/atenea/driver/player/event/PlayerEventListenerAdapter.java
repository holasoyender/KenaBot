/*
 * File:          PlayerEventListenerAdapter.java
 * Path:          v3\src\main\java\atenea\driver\player\event\PlayerEventListenerAdapter.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 23:59:04 +02:00
 *
 * SHA-256: 40b50ee169bbc04ac050677565ee9688c50c8e0cfceef228eddc3fe8528a77a5
 *
 */


package atenea.driver.player.event;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import atenea.driver.player.IPlayer;

public class PlayerEventListenerAdapter implements IPlayerEventListener {

    /**
     * @param player Audio player
     */
    public void onPlayerPause(IPlayer player) {
        // Adapter dummy method
    }

    /**
     * @param player Audio player
     */
    public void onPlayerResume(IPlayer player) {
        // Adapter dummy method
    }

    /**
     * @param player Audio player
     * @param track Audio track that started
     */
    public void onTrackStart(IPlayer player, AudioTrack track) {
        // Adapter dummy method
    }

    /**
     * @param player Audio player
     * @param track Audio track that ended
     * @param endReason The reason why the track stopped playing
     */
    public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Adapter dummy method
    }

    /**
     * @param player Audio player
     * @param track Audio track where the exception occurred
     * @param exception The exception that occurred
     */
    public void onTrackException(IPlayer player, AudioTrack track, Exception exception) {
        // Adapter dummy method
    }

    /**
     * @param player Audio player
     * @param track Audio track where the exception occurred
     * @param thresholdMs The wait threshold that was exceeded for this event to trigger
     */
    public void onTrackStuck(IPlayer player, AudioTrack track, long thresholdMs) {
        // Adapter dummy method
    }

    /**
     * WebSocket close event
     * @param player Audio player
     * @param code error code
     * @param reason reason of websocket close
     * @param remote true if the websocket was closed by the discord, false if the websocket was closed by the atenea
     */
    public void onWebSocketClosed(IPlayer player, int code, String reason, boolean remote) {
        // Adapter dummy method
    }

    @Override
    public void onEvent(PlayerEvent event) {
        if (event instanceof PlayerPauseEvent) {
            onPlayerPause(event.getPlayer());
        } else if (event instanceof PlayerResumeEvent) {
            onPlayerResume(event.getPlayer());
        } else if (event instanceof TrackStartEvent) {
            onTrackStart(event.getPlayer(), ((TrackStartEvent) event).getTrack());
        } else if (event instanceof TrackEndEvent) {
            onTrackEnd(event.getPlayer(), ((TrackEndEvent) event).getTrack(), ((TrackEndEvent) event).getReason());
        } else if (event instanceof TrackExceptionEvent) {
            onTrackException(event.getPlayer(), ((TrackExceptionEvent) event).getTrack(), ((TrackExceptionEvent) event).getException());
        } else if (event instanceof TrackStuckEvent) {
            onTrackStuck(event.getPlayer(), ((TrackStuckEvent) event).getTrack(), ((TrackStuckEvent) event).getThresholdMs());
        } else if (event instanceof WebSocketClosedEvent){
            onWebSocketClosed(event.getPlayer(), ((WebSocketClosedEvent) event).getCode(), ((WebSocketClosedEvent) event).getReason(), ((WebSocketClosedEvent) event).isRemote());
        }
    }
}
