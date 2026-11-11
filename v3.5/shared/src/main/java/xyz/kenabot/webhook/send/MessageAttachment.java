/*
 * File:          MessageAttachment.java
 * Path:          v3.5\shared\src\main\java\xyz\kenabot\webhook\send\MessageAttachment.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 14223eff41f5b782f7f08b6b7eed7ccf62891fbf5cbf2af3abb9d5489d2e58e7
 *
 */



package xyz.kenabot.webhook.send;

import xyz.kenabot.webhook.IOUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Internal representation of attachments for outgoing messages
 */
public class MessageAttachment {
    private final String name;
    private final byte[] data;

    MessageAttachment(@NotNull String name, @NotNull byte[] data) {
        this.name = name;
        this.data = data;
    }

    MessageAttachment(@NotNull String name, @NotNull InputStream stream) throws IOException {
        this.name = name;
        try (InputStream data = stream) {
            this.data = IOUtil.readAllBytes(data);
        }
    }

    MessageAttachment(@NotNull String name, @NotNull File file) throws IOException {
        this(name, new FileInputStream(file));
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public byte[] getData() {
        return data;
    }
}
