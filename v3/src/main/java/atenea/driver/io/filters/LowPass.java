/*
 * File:          LowPass.java
 * Path:          v3\src\main\java\atenea\driver\io\filters\LowPass.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 23:59:04 +02:00
 *
 * SHA-256: 41191b508a90df8b7b7da84dd3289a0c5415526e597ce8b2c559071eb3e43670
 *
 */


package atenea.driver.io.filters;

import org.slf4j.helpers.CheckReturnValue;

@SuppressWarnings("unused")
public class LowPass {
    private float smoothing = 20f;

    public float getSmoothing() {
        return smoothing;
    }

    @CheckReturnValue
    public LowPass setSmoothing(float smoothing) {
        this.smoothing = smoothing;
        return this;
    }
}
