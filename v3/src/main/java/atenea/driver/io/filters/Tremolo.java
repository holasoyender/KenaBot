/*
 * File:          Tremolo.java
 * Path:          v3\src\main\java\atenea\driver\io\filters\Tremolo.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 23:59:04 +02:00
 *
 * SHA-256: 12326e4a7a07e8dd45133d69f7b13221bc60d6e471530e7995475ae491b2c0c5
 *
 */


package atenea.driver.io.filters;

import org.slf4j.helpers.CheckReturnValue;

@SuppressWarnings("unused")
public class Tremolo {
    private float frequency = 2.0f;
    private float depth = 0.5f;

    public float getFrequency() {
        return frequency;
    }

    @CheckReturnValue
    public Tremolo setFrequency(float frequency) {
        if (frequency <= 0) throw new IllegalArgumentException("Frequency must be >0");
        this.frequency = frequency;
        return this;
    }

    public float getDepth() {
        return depth;
    }

    @CheckReturnValue
    public Tremolo setDepth(float depth) {
        if (depth <= 0 || depth >= 1) throw new IllegalArgumentException("Frequency must be >0 and <=1");
        this.depth = depth;
        return this;
    }
}
