/*
 * File:          Timescale.java
 * Path:          v3\src\main\java\atenea\driver\io\filters\Timescale.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 23:59:04 +02:00
 *
 * SHA-256: 6cc1fb6acfbb2e75026caf978a75192fefc979220e898572de55ae49d346e2b0
 *
 */


package atenea.driver.io.filters;

import org.slf4j.helpers.CheckReturnValue;

@SuppressWarnings("unused")
public class Timescale {
    private float speed = 1.0f;
    private float pitch= 1.0f;
    private float rate = 1.0f;

    public float getSpeed() {
        return speed;
    }

    @CheckReturnValue
    public Timescale setSpeed(float speed) {
        if (speed < 0) throw new IllegalArgumentException("Speed must be greater than 0");
        this.speed = speed;
        return this;
    }

    public float getPitch() {
        return pitch;
    }

    @CheckReturnValue
    public Timescale setPitch(float pitch) {
        if (pitch < 0) throw new IllegalArgumentException("Pitch must be greater than 0");
        this.pitch = pitch;
        return this;
    }

    public float getRate() {
        return rate;
    }

    @CheckReturnValue
    public Timescale setRate(float rate) {
        if (rate < 0) throw new IllegalArgumentException("Rate must be greater than 0");
        this.rate = rate;
        return this;
    }
}
