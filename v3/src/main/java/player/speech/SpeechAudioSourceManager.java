/*
 * File:          SpeechAudioSourceManager.java
 * Path:          v3\src\main\java\player\speech\SpeechAudioSourceManager.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-29 12:59:00 +02:00
 *
 * SHA-256: 5df3523a0f7ae80e6ebfdaf1864a493ddc9ab10554f771725c2db8a9a3317b60
 *
 */


package player.speech;

import player.AbstractHttpSource;
import player.IdentifiedAudioReference;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SpeechAudioSourceManager extends AbstractHttpSource {

    private static final String PREFIX = "speak:";
    private static final String GOOGLE_TRANSLATE_URL = "https://translate.google.com/translate_tts" +
        "?tl=es-ES" +
        "&q=%query%" +
        "&ie=UTF-8&total=1&idx=0" +
        "&text" + "len=%length%" +
        "&client=tw-ob";

    private final String templateURL;

    public SpeechAudioSourceManager() {
        this.templateURL = GOOGLE_TRANSLATE_URL;
    }

    @Override
    public String getSourceName() {
        return "speak";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        if (!reference.identifier.startsWith(PREFIX) || reference.identifier.length() <= PREFIX.length()) {
            return null;
        }

        String data = reference.identifier.substring(PREFIX.length());
        data = data
            // Remove whitespaces at the end
            .trim()
            // Remove whitespaces at the front
            .replaceAll("^\\s+", "");

        final String encoded = URLEncoder.encode(data, StandardCharsets.UTF_8);

        final String mp3URL = templateURL
            .replace("%length%", Integer.toString(data.length()))
            .replace("%query%", encoded);

        return new IdentifiedAudioReference(mp3URL, "", "tts:tts");
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return true;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) {
        // empty because we don't need them
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
        return new SpeechAudioTrack(trackInfo, this);
    }
}
