/*
 * File:          AteneaUtil.java
 * Path:          v3\src\main\java\atenea\driver\AteneaUtil.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-07 01:11:56 +02:00
 *
 * SHA-256: aaa833c0d42b6dbb65036a061db765c1a6842ee7c6a110caf1383d77112e0af0
 *
 */


package atenea.driver;

import com.neovisionaries.i18n.CountryCode;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.io.MessageInput;
import com.sedmelluq.discord.lavaplayer.tools.io.MessageOutput;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import atenea.driver.player.AteneaPlayer;
import player.applemusic.AppleMusicSourceManager;
import player.deezer.DeezerAudioSourceManager;
import player.reddit.RedditAudioSourceManager;
import player.spotify.SpotifySourceManager;
import player.tiktok.TikTokAudioSourceManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class AteneaUtil {

    private static final AudioPlayerManager PLAYER_MANAGER;

    static {
        PLAYER_MANAGER = new DefaultAudioPlayerManager();

        /* These are only to encode/decode messages */

        PLAYER_MANAGER.registerSourceManager(new SpotifySourceManager(null, CountryCode.ES.name(), PLAYER_MANAGER));
        PLAYER_MANAGER.registerSourceManager(new AppleMusicSourceManager(null, null, "ES", PLAYER_MANAGER));
        PLAYER_MANAGER.registerSourceManager(new DeezerAudioSourceManager("05e23efdae9f4e553c184dd141187b3fae35c3f962b409c6188b8aeb3767452967239c66c3e7296b687addaa1f8dc34b20b3ece16c241817953bbee07fc70fe4a355237daca1006978731f014ab2859ce772be5bf98caa5456cc76ed863b8562"));
        PLAYER_MANAGER.registerSourceManager(new YoutubeAudioSourceManager(true, "", ""));
        PLAYER_MANAGER.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        PLAYER_MANAGER.registerSourceManager(new BandcampAudioSourceManager());
        PLAYER_MANAGER.registerSourceManager(new TwitchStreamAudioSourceManager());
        PLAYER_MANAGER.registerSourceManager(new TikTokAudioSourceManager());
        PLAYER_MANAGER.registerSourceManager(new RedditAudioSourceManager());

    }

    /**
     *
     * @param player the atenea player that holds the track with data
     * @param message the Base64 audio track
     * @return the AudioTrack with the user data stored in the player
     * @throws IOException if there is an IO problem
     */
    public static AudioTrack toAudioTrackWithData(AteneaPlayer player, String message) throws IOException {
        AudioTrack storedTrack = player.getPlayingTrack();
        AudioTrack messageTrack = toAudioTrack(message);

        if (storedTrack != null && storedTrack.getUserData() != null) {
            messageTrack.setUserData(storedTrack.getUserData());
        }

        return messageTrack;
    }

    /**
     *
     * @param message the Base64 audio track
     * @return the AudioTrack
     * @throws IOException if there is an IO problem
     */
    public static AudioTrack toAudioTrack(String message) throws IOException {
        return toAudioTrack(Base64.getDecoder().decode(message));
    }

    /**
     * @param message the unencoded audio track
     * @return the AudioTrack
     * @throws IOException if there is an IO problem
     */
    @SuppressWarnings("WeakerAccess")
    public static AudioTrack toAudioTrack(byte[] message) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(message);
        return PLAYER_MANAGER.decodeTrack(new MessageInput(bais)).decodedTrack;
    }

    /**
     * @param track the track to serialize
     * @return the serialized track a Base64 string
     * @throws IOException if there is an IO problem
     */
    public static String toMessage(AudioTrack track) throws IOException {
        return Base64.getEncoder().encodeToString(toBinary(track));
    }

    /**
     * @param track the track to serialize
     * @return the serialized track as binary
     * @throws IOException if there is an IO problem
     */
    @SuppressWarnings("WeakerAccess")
    public static byte[] toBinary(AudioTrack track) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PLAYER_MANAGER.encodeTrack(new MessageOutput(baos), track);
        return baos.toByteArray();
    }

    public static int getShardFromSnowflake(String snowflake, int numShards) {
        return (int) ((Long.parseLong(snowflake) >> 22) % numShards);
    }

}
