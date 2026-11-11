/*
 * File:          MpegTrack.java
 * Path:          v3\src\main\java\player\MpegTrack.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-29 12:59:00 +02:00
 *
 * SHA-256: aaf718bca3de296e081e8cbbd45fced13faba2a0d9b8e88ad9017412ef48c88e
 *
 */


package player;

import com.sedmelluq.discord.lavaplayer.container.mpeg.MpegAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.InternalAudioTrack;

public abstract class MpegTrack extends Mp3Track {
    public MpegTrack(AudioTrackInfo trackInfo, AbstractHttpSource manager) {
        super(trackInfo, manager);
    }

    @Override
    protected InternalAudioTrack createAudioTrack(AudioTrackInfo trackInfo, SeekableInputStream stream) {
        return new MpegAudioTrack(trackInfo, stream);
    }
}