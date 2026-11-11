/*
 * File:          MirroringAudioSourceManager.java
 * Path:          v3\src\main\java\player\mirror\MirroringAudioSourceManager.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-06-23 15:47:43 +02:00
 *
 * SHA-256: 6f1a107b03eafc173d2a3127c0734d6165c91ba109bbcd42ebf3ad41b4cae087
 *
 */


package player.mirror;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.DataFormatTools;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.io.DataOutput;
import java.io.IOException;

public abstract class MirroringAudioSourceManager implements AudioSourceManager {

    public static final String ISRC_PATTERN = "%ISRC%";
    public static final String QUERY_PATTERN = "%QUERY%";
    protected final AudioPlayerManager audioPlayerManager;
    protected String[] providers = {
            "ytsearch:\"" + ISRC_PATTERN + "\"",
            "ytmsearch:" + QUERY_PATTERN
    };

    protected MirroringAudioSourceManager(String[] providers, AudioPlayerManager audioPlayerManager) {
        if (providers != null && providers.length > 0) {
            this.providers = providers;
        }
        this.audioPlayerManager = audioPlayerManager;
    }

    public String[] getProviders() {
        return this.providers;
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return this.audioPlayerManager;
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return true;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) throws IOException {
        var isrcAudioTrack = ((MirroringAudioTrack) track);
        DataFormatTools.writeNullableText(output, isrcAudioTrack.getISRC());
        DataFormatTools.writeNullableText(output, isrcAudioTrack.getArtworkURL());
    }

}