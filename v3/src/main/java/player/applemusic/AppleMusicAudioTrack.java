/*
 * File:          AppleMusicAudioTrack.java
 * Path:          v3\src\main\java\player\applemusic\AppleMusicAudioTrack.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-12-26 22:13:05 +01:00
 *
 * SHA-256: 98921e3b4df8a1e2a6344648beb27816445c1b4db01dae869f392e0bac9ff2c5
 *
 */


package player.applemusic;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import player.mirror.MirroringAudioTrack;

public class AppleMusicAudioTrack extends MirroringAudioTrack {

    public AppleMusicAudioTrack(AudioTrackInfo trackInfo, String isrc, String artworkURL, AppleMusicSourceManager sourceManager) {
        super(trackInfo, isrc, artworkURL, sourceManager);
    }

    @Override
    protected AudioTrack makeShallowClone() {
        return new AppleMusicAudioTrack(this.trackInfo, this.isrc, this.artworkURL, (AppleMusicSourceManager) this.sourceManager);
    }

}