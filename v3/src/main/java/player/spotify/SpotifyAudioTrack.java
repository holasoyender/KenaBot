/*
 * File:          SpotifyAudioTrack.java
 * Path:          v3\src\main\java\player\spotify\SpotifyAudioTrack.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-02-09 18:35:05 +01:00
 *
 * SHA-256: 301fe926b93b5416e839d7390113f941368d6c10a5817e315ac98d500f4bc175
 *
 */


package player.spotify;

import player.mirror.MirroringAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
public class SpotifyAudioTrack extends MirroringAudioTrack {

    public SpotifyAudioTrack(AudioTrackInfo trackInfo, String isrc, String artworkURL, Boolean explicit, SpotifySourceManager sourceManager) {
        super(trackInfo, isrc, artworkURL, explicit, sourceManager);
    }

    public SpotifyAudioTrack(AudioTrackInfo trackInfo, String isrc, String artworkURL, SpotifySourceManager sourceManager) {
        super(trackInfo, isrc, artworkURL,false, sourceManager);
    }

    @Override
    protected AudioTrack makeShallowClone() {
        return new SpotifyAudioTrack(this.trackInfo, this.isrc, this.artworkURL, this.explicit, (SpotifySourceManager) this.sourceManager);
    }

}