/*
 * File:          Mp3Track.java
 * Path:          v3\src\main\java\player\Mp3Track.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-31 11:18:00 +02:00
 *
 * SHA-256: d7d5099773e19043da63e97baca706b64cae4e25abbd366d5e956dea9bcc72f0
 *
 */


package player;

import com.sedmelluq.discord.lavaplayer.container.mp3.Mp3AudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.PersistentHttpStream;
import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.InternalAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import java.net.URI;


public class Mp3Track extends DelegatedAudioTrack {

    private final AbstractHttpSource manager;

    public Mp3Track(AudioTrackInfo trackInfo, AbstractHttpSource manager) {
        super(trackInfo);
        this.manager = manager;
    }

    protected HttpInterface getHttpInterface() {
        return this.manager.getHttpInterface();
    }

    @Override
    public void process(LocalAudioTrackExecutor executor) throws Exception {
        try (HttpInterface httpInterface = getHttpInterface()) {
            loadStream(executor, httpInterface);
        }
    }

    protected void loadStream(LocalAudioTrackExecutor localExecutor, HttpInterface httpInterface) throws Exception {
        final String trackUrl = getPlaybackUrl();
        // Setting contentLength (last param) to null makes it default to Long.MAX_VALUE
        try (PersistentHttpStream stream = new PersistentHttpStream(httpInterface, new URI(trackUrl), this.getTrackDuration())) {
            processDelegate(createAudioTrack(this.trackInfo, stream), localExecutor);
        }
    }

    protected InternalAudioTrack createAudioTrack(AudioTrackInfo trackInfo, SeekableInputStream stream) {
        return new Mp3AudioTrack(trackInfo, stream);
    }

    protected long getTrackDuration() {
        return this.trackInfo.length;
    }

    protected String getPlaybackUrl() {
        return this.trackInfo.identifier;
    }

    @Override
    public AbstractHttpSource getSourceManager() {
        return manager;
    }
}