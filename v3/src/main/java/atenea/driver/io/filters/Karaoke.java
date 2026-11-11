/*
 * File:          Karaoke.java
 * Path:          v3\src\main\java\atenea\driver\io\filters\Karaoke.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 23:59:04 +02:00
 *
 * SHA-256: c5d2662cb6984a492ec93ec471480a38d41ac0eabf5298715549834d4708e575
 *
 */


package atenea.driver.io.filters;

import org.slf4j.helpers.CheckReturnValue;

@SuppressWarnings("unused")
public class Karaoke {
    private float level = 1.0f;
    private float monoLevel = 1.0f;
    private float filterBand = 220.0f;
    private float filterWidth = 100.0f;

    public float getLevel() {
        return level;
    }

    @CheckReturnValue
    public Karaoke setLevel(float level) {
        this.level = level;
        return this;
    }

    public float getMonoLevel() {
        return monoLevel;
    }

    @CheckReturnValue
    public Karaoke setMonoLevel(float monoLevel) {
        this.monoLevel = monoLevel;
        return this;
    }

    public float getFilterBand() {
        return filterBand;
    }

    @CheckReturnValue
    public Karaoke setFilterBand(float filterBand) {
        this.filterBand = filterBand;
        return this;
    }

    public float getFilterWidth() {
        return filterWidth;
    }

    @CheckReturnValue
    public Karaoke setFilterWidth(float filterWidth) {
        this.filterWidth = filterWidth;
        return this;
    }
}
