/*
 * File:          Rotation.java
 * Path:          v3\src\main\java\atenea\driver\io\filters\Rotation.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 23:59:04 +02:00
 *
 * SHA-256: 24debc43eef3f8e280417feeec65e50534a3168495070d8faa32f9a180d478df
 *
 */


package atenea.driver.io.filters;

import org.slf4j.helpers.CheckReturnValue;

@SuppressWarnings("unused")
public class Rotation{
    private float frequency = 2.0f;

    public float getFrequency() {
        return frequency;
    }

    @CheckReturnValue
    public Rotation setFrequency(float frequency) {
        this.frequency = frequency;
        return this;
    }

}
