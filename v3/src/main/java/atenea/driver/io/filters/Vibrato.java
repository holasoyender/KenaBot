/*
 * File:          Vibrato.java
 * Path:          v3\src\main\java\atenea\driver\io\filters\Vibrato.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: ceac5891dcb0d37b19b15c6aad5343175dee39f76bd48934015da38479ff5621
 *
 */


package atenea.driver.io.filters;

import org.slf4j.helpers.CheckReturnValue;

@SuppressWarnings("unused")
public class Vibrato {
    private float frequency = 2.0f;
    private float depth = 0.5f;

    public float getFrequency() {
        return frequency;
    }

    @CheckReturnValue
    public Vibrato setFrequency(float frequency) {
        if (frequency <= 0 || frequency > 14) throw new IllegalArgumentException("Frequency must be >0 and <=14");
        this.frequency = frequency;
        return this;
    }

    public float getDepth() {
        return depth;
    }

    @CheckReturnValue
    public Vibrato setDepth(float depth) {
        if (depth <= 0 || depth >= 1) throw new IllegalArgumentException("Depth must be >0 and <=1");
        this.depth = depth;
        return this;
    }
}
