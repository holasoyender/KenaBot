/*
 * File:          AteneaPlayer.java
 * Path:          v3\src\main\java\atenea\driver\player\AteneaPlayer.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-07 01:11:56 +02:00
 *
 * SHA-256: b73f94f079dd6b4a3a58d7e201d9570fa2f77172c244cd08db1d2a99490f0105
 *
 */


package atenea.driver.player;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import atenea.driver.AteneaUtil;
import atenea.driver.io.AteneaSocket;
import atenea.driver.io.Link;
import atenea.driver.io.filters.*;
import atenea.driver.player.event.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.helpers.CheckReturnValue;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AteneaPlayer implements IPlayer {

    private AudioTrack track = null;
    private boolean paused = false;
    private int volume = 100;
    private long updateTime = -1;
    private long position = -1;
    /** Lazily initialized */
    private Filters filters = null;
    private boolean connected = false;

    private final Link link;
    private final List<IPlayerEventListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Constructor only for internal use
     *
     * @param link the parent link
     */
    public AteneaPlayer(Link link) {
        this.link = link;
        addListener(new AteneaInternalPlayerEventHandler());
    }

    /**
     * Invoked by {@link Link} to make sure we keep playing music on the new node
     * <p>
     * Used when we are moved to a new socket
     */
    public void onNodeChange() {
        AudioTrack track = getPlayingTrack();
        if (track != null) {
            track.setPosition(getTrackPosition());
            playTrack(track);
        }

    }

    @Override
    public AudioTrack getPlayingTrack() {
        return track;
    }

    @Override
    public void playTrack(AudioTrack track) {
        try {
            position = track.getPosition();
            TrackData trackData = track.getUserData(TrackData.class);

            JSONObject json = new JSONObject();
            json.put("op", "play");
            json.put("guildId", link.getGuildId());
            json.put("track", AteneaUtil.toMessage(track));
            json.put("startTime", position);
            if (trackData != null) {
                json.put("startTime", trackData.startPos);
                json.put("endTime", trackData.endPos);
            }
            json.put("pause", paused);
            json.put("volume", volume);
            //noinspection ConstantConditions
            link.getNode(true).send(json.toString());

            updateTime = System.currentTimeMillis();
            this.track = track;
            emitEvent(new TrackStartEvent(this, track));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopTrack() {
        track = null;

        AteneaSocket node = link.getNode(false);
        if (node == null) return;
        JSONObject json = new JSONObject();
        json.put("op", "stop");
        json.put("guildId", link.getGuildId());
        node.send(json.toString());
    }

    @Override
    public void setPaused(boolean pause) {
        if (pause == paused) return;
        AteneaSocket node = link.getNode(false);
        if (node != null) {
            JSONObject json = new JSONObject();
            json.put("op", "pause");
            json.put("guildId", link.getGuildId());
            json.put("pause", pause);
            node.send(json.toString());
        }
        paused = pause;

        if (pause) {
            emitEvent(new PlayerPauseEvent(this));
        } else {
            emitEvent(new PlayerResumeEvent(this));
        }
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public long getTrackPosition() {
        if (getPlayingTrack() == null) throw new IllegalStateException("Not currently playing anything");

        if (!paused) {
            // Account for the time since our last update
            long timeDiff = System.currentTimeMillis() - updateTime;
            return Math.min(position + timeDiff, track.getDuration());
        } else {
            return Math.min(position, track.getDuration());
        }

    }

    @Override
    public void seekTo(long position) {
        if (getPlayingTrack() == null) throw new IllegalStateException("Not currently playing anything");
        if (!getPlayingTrack().isSeekable()) throw new IllegalStateException("Track cannot be seeked");

        JSONObject json = new JSONObject();
        json.put("op", "seek");
        json.put("guildId", link.getGuildId());
        json.put("position", position);
        //noinspection ConstantConditions
        link.getNode(true).send(json.toString());
        
        this.position = position;
    }

    /**
     * @see AteneaPlayer#getFilters()
     */
    @Override
    public void setVolume(int volume) {
        volume = Math.min(1000, Math.max(0, volume)); // Lavaplayer bounds
        this.volume = volume;

        AteneaSocket node = link.getNode(false);
        if (node == null) return;

        JSONObject json = new JSONObject();
        json.put("op", "volume");
        json.put("guildId", link.getGuildId());
        json.put("volume", volume);
        node.send(json.toString());
    }

    @Override
    public int getVolume() {
        return (int) (getFilters().getVolume() * 100);
    }

    /**
     * @return a builder that allows setting filters such as volume, an equalizer, etc.
     * @see Filters#commit()
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    @NotNull
    @CheckReturnValue
    public Filters getFilters() {
        if (filters == null) filters = new Filters(this, this::onCommit);
        return filters;
    }

    public void provideState(JSONObject json) {
        updateTime = json.getLong("time");
        position = json.optLong("position", 0);
        connected = json.optBoolean("connected", true);
    }

    @Override
    public void addListener(IPlayerEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(IPlayerEventListener listener) {
        listeners.remove(listener);
    }

    public void emitEvent(PlayerEvent event) {
        listeners.forEach(listener -> listener.onEvent(event));
    }

    void clearTrack() {
        track = null;
    }

    @SuppressWarnings({"unused"})
    public Link getLink() {
        return link;
    }

    private void onCommit() {
        AteneaSocket node = link.getNode(false);
        if (node == null) return;

        JSONObject json = new JSONObject();
        json.put("op", "filters");
        json.put("guildId", link.getGuildId());

        // Volume
        json.put("volume", filters.getVolume());

        // Equalizer
        JSONArray bands = new JSONArray();
        int i = -1;
        for (float f : filters.getBands()) {
            i++;
            if (f == 0.0f) continue;
            JSONObject obj = new JSONObject();
            obj.put("band", i);
            obj.put("gain", f);
            bands.put(obj);
        }
        if (bands.length() > 0) json.put("equalizer", bands);

        Timescale timescale = filters.getTimescale();
        if (timescale != null) {
            JSONObject obj = new JSONObject();
            obj.put("speed", timescale.getSpeed());
            obj.put("pitch", timescale.getPitch());
            obj.put("rate", timescale.getRate());
            json.put("timescale", obj);
        }

        Karaoke karaoke = filters.getKaraoke();
        if (karaoke != null) {
            JSONObject obj = new JSONObject();
            obj.put("level", karaoke.getLevel());
            obj.put("monoLevel", karaoke.getMonoLevel());
            obj.put("filterBand", karaoke.getFilterBand());
            obj.put("filterWidth", karaoke.getFilterWidth());
            json.put("karaoke", obj);
        }

        Tremolo tremolo = filters.getTremolo();
        if (tremolo != null) {
            JSONObject obj = new JSONObject();
            obj.put("frequency", tremolo.getFrequency());
            obj.put("depth", tremolo.getDepth());
            json.put("tremolo", obj);
        }

        Vibrato vibrato = filters.getVibrato();
        if (vibrato != null) {
            JSONObject obj = new JSONObject();
            obj.put("frequency", vibrato.getFrequency());
            obj.put("depth", vibrato.getDepth());
            json.put("vibrato", obj);
        }

        Rotation rotation = filters.getRotation();
        if (rotation != null) {
            JSONObject obj = new JSONObject();
            obj.put("rotationHz", rotation.getFrequency());
            json.put("rotation", obj);
        }

        Distortion distortion = filters.getDistortion();
        if (distortion != null) {
            JSONObject obj = new JSONObject();
            obj.put("sinOffset", distortion.getSinOffset());
            obj.put("sinScale", distortion.getSinScale());
            obj.put("cosOffset", distortion.getCosOffset());
            obj.put("cosScale", distortion.getCosScale());
            obj.put("tanOffset", distortion.getTanOffset());
            obj.put("tanScale", distortion.getTanScale());
            obj.put("offset", distortion.getOffset());
            obj.put("scale", distortion.getScale());
            json.put("distortion", obj);
        }

        ChannelMix channelMix = filters.getChannelMix();
        if (channelMix != null) {
            JSONObject obj = new JSONObject();
            obj.put("leftToLeft", channelMix.getLeftToLeft());
            obj.put("leftToRight", channelMix.getLeftToRight());
            obj.put("rightToLeft", channelMix.getRightToLeft());
            obj.put("rightToRight", channelMix.getRightToRight());
            json.put("channelMix", obj);
        }

        LowPass lowPass = filters.getLowPass();
        if (lowPass != null) {
            JSONObject obj = new JSONObject();
            obj.put("smoothing", lowPass.getSmoothing());
            json.put("lowPass", obj);
        }

        node.send(json.toString());
    }

    /**
     * @return Whether or not the Atenea player is connected to the gateway
     */
    public boolean isConnected() {
        return connected;
    }
}
