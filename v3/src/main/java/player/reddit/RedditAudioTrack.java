/*
 * File:          RedditAudioTrack.java
 * Path:          v3\src\main\java\player\reddit\RedditAudioTrack.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-29 12:59:00 +02:00
 *
 * SHA-256: 71c3d4f3380e0bc507d3bffd35b654ca9528fd56e4f1f5e007232ac722b96874
 *
 */


package player.reddit;

import player.AbstractHttpSource;
import player.MpegTrack;
import com.sedmelluq.discord.lavaplayer.tools.Units;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

public class RedditAudioTrack extends MpegTrack {
    public RedditAudioTrack(AudioTrackInfo trackInfo, AbstractHttpSource manager) {
        super(trackInfo, manager);
    }

    @Override
    protected String getPlaybackUrl() {
        return getPlaybackUrl(this.trackInfo.identifier);
    }

    static String getPlaybackUrl(String id) {
        return "https://v.redd.it/" + id + "/DASH_audio.mp4?source=fallback";
    }

    @Override
    protected long getTrackDuration() {
        return Units.CONTENT_LENGTH_UNKNOWN;
    }

    @Override
    protected AudioTrack makeShallowClone() {
        return new RedditAudioTrack(this.trackInfo, this.getSourceManager());
    }
}
