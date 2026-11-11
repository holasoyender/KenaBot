/*
 * File:          Filters.java
 * Path:          v3\src\main\java\atenea\driver\io\filters\Filters.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 23:59:04 +02:00
 *
 * SHA-256: 50adf2e8f829884c7be8372f6aeb32045680f80b2b0c086b145c78c149132e0e
 *
 */


package atenea.driver.io.filters;

import atenea.driver.player.AteneaPlayer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.helpers.CheckReturnValue;

import static com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer.BAND_COUNT;

@SuppressWarnings("unused")
public class Filters {

    @SuppressWarnings("WeakerAccess")
    public static float DEFAULT_VOLUME = 1.0f;

    private final AteneaPlayer player;
    private final Runnable onCommit;
    private float volume = DEFAULT_VOLUME;
    private float[] bands = new float[BAND_COUNT];
    private Karaoke karaoke = null;
    private Timescale timescale = null;
    private Tremolo tremolo = null;
    private Vibrato vibrato = null;
    private Rotation rotation = null;
    private Distortion distortion = null;
    private ChannelMix channelMix = null;
    private LowPass lowPass = null;

    /**
     * Intended for internal use only
     */
    public Filters(AteneaPlayer player, Runnable onCommit) {
        this.player = player;
        this.onCommit = onCommit;
    }

    public AteneaPlayer getPlayer() {
        return player;
    }

    public float[] getBands() {
        return bands;
    }

    /**
     * Configures the equalizer.
     *
     * @param band the band to change, values 0-14
     * @param gain the gain in volume for the given band, range -0.25 (mute) to 1.0 (quadruple).
     */
    @CheckReturnValue
    public Filters setBand(int band, float gain) {
        if (gain < -0.25 || gain > 1) throw new IllegalArgumentException("Gain must be -0.25 to 1.0");
        bands[band] = gain;
        return this;
    }

    public float getVolume() {
        return volume;
    }

    /**
     * @param volume where 1.0f is regular volume. Values greater than 1.0f are allowed, but may cause clipping.
     */
    @CheckReturnValue
    public Filters setVolume(float volume) {
        if (volume < 0) throw new IllegalArgumentException("Volume must be greater than 0");
        this.volume = volume;
        return this;
    }

    @Nullable
    public Karaoke getKaraoke() {
        return karaoke;
    }

    @CheckReturnValue
    public Filters setKaraoke(Karaoke karaoke) {
        this.karaoke = karaoke;
        return this;
    }

    @Nullable
    public Timescale getTimescale() {
        return timescale;
    }

    @CheckReturnValue
    public Filters setTimescale(Timescale timescale) {
        this.timescale = timescale;
        return this;
    }

    @Nullable
    public Tremolo getTremolo() {
        return tremolo;
    }

    @CheckReturnValue
    public Filters setTremolo(Tremolo tremolo) {
        this.tremolo = tremolo;
        return this;
    }

    @Nullable
    public Vibrato getVibrato() {
        return vibrato;
    }

    @CheckReturnValue
    public Filters setVibrato(Vibrato vibrato) {
        this.vibrato = vibrato;
        return this;
    }

    @Nullable
    public Rotation getRotation() {
        return rotation;
    }

    @CheckReturnValue
    public Filters setRotation(Rotation rotation) {
        this.rotation = rotation;
        return this;
    }

    @Nullable
    public Distortion getDistortion() {
        return distortion;
    }

    @CheckReturnValue
    public Filters setDistortion(Distortion distortion) {
        this.distortion = distortion;
        return this;
    }

    @Nullable
    public ChannelMix getChannelMix() {
        return channelMix;
    }

    @CheckReturnValue
    public Filters setChannelMix(ChannelMix channelMix) {
        this.channelMix = channelMix;
        return this;
    }

    @Nullable
    public LowPass getLowPass() {
        return lowPass;
    }

    @CheckReturnValue
    public Filters setLowPass(LowPass lowPass) {
        this.lowPass = lowPass;
        return this;
    }

    /**
     * Resets this player's filters.
     */
    @CheckReturnValue
    public Filters clear() {
        volume = DEFAULT_VOLUME;
        bands = new float[BAND_COUNT];
        timescale = null;
        karaoke = null;
        tremolo = null;
        vibrato = null;
        rotation = null;
        distortion = null;
        channelMix = null;
        lowPass = null;
        return this;
    }

    /**
     * Commits these filters to the Atenea server.
     *
     * The client may choose to commit changes at any time, even if this method is never invoked.
     */
    public void commit() {
        onCommit.run();
    }

}
