/*
 * File:          SpeechAudioTrack.java
 * Path:          v3\src\main\java\player\speech\SpeechAudioTrack.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-29 12:59:00 +02:00
 *
 * SHA-256: 4d08975d582bd1c72a688a4f2d25ab1481a1a76b0339aa43867910ab417b213e
 *
 */


package player.speech;

import com.sedmelluq.discord.lavaplayer.tools.Units;
import player.AbstractHttpSource;
import player.Mp3Track;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

public class SpeechAudioTrack extends Mp3Track {

    public SpeechAudioTrack(AudioTrackInfo trackInfo, AbstractHttpSource manager) {
        super(trackInfo, manager);
    }

    @Override
    protected String getPlaybackUrl() {
        return getPlaybackUrl(this.trackInfo.uri);
    }

    static String getPlaybackUrl(String uri) {
        return uri;
    }

    @Override
    protected long getTrackDuration() {
        return Units.secondsToMillis(0);
    }

    @Override
    public AudioTrack makeShallowClone() {
        return new SpeechAudioTrack(this.trackInfo, this.getSourceManager());
    }

}
