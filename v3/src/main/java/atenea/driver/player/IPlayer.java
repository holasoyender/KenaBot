/*
 * File:          IPlayer.java
 * Path:          v3\src\main\java\atenea\driver\player\IPlayer.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: 24e64c1585f5a15f7dd96c4dae9895e1ed6690c12e1006bb8a1afdff203a9c46
 *
 */


package atenea.driver.player;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import atenea.driver.player.event.IPlayerEventListener;

public interface IPlayer {

    AudioTrack getPlayingTrack();

    void playTrack(AudioTrack track);

    void stopTrack();

    void setPaused(boolean b);

    boolean isPaused();

    long getTrackPosition();

    void seekTo(long position);

    void setVolume(int volume);

    int getVolume();

    void addListener(IPlayerEventListener listener);

    void removeListener(IPlayerEventListener listener);

}
