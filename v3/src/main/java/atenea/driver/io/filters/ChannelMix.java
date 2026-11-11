/*
 * File:          ChannelMix.java
 * Path:          v3\src\main\java\atenea\driver\io\filters\ChannelMix.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 23:59:04 +02:00
 *
 * SHA-256: f62a95ba9b93b4cba2106ebb48a02fd2d8c3850b47d07e86b0263b347092ee0f
 *
 */


package atenea.driver.io.filters;

import org.slf4j.helpers.CheckReturnValue;

@SuppressWarnings("unused")
public class ChannelMix {
    private float leftToLeft = 1f;
    private float leftToRight = 0f;
    private float rightToLeft = 0f;
    private float rightToRight = 1f;

    public float getLeftToLeft() {
        return leftToLeft;
    }

    @CheckReturnValue
    public ChannelMix setLeftToLeft(float leftToLeft) {
        this.leftToLeft = leftToLeft;
        return this;
    }

    public float getLeftToRight() {
        return leftToRight;
    }

    @CheckReturnValue
    public ChannelMix setLeftToRight(float leftToRight) {
        this.leftToRight = leftToRight;
        return this;
    }

    public float getRightToLeft() {
        return rightToLeft;
    }

    @CheckReturnValue
    public ChannelMix setRightToLeft(float rightToLeft) {
        this.rightToLeft = rightToLeft;
        return this;
    }

    public float getRightToRight() {
        return rightToRight;
    }

    @CheckReturnValue
    public ChannelMix setRightToRight(float rightToRight) {
        this.rightToRight = rightToRight;
        return this;
    }
}
