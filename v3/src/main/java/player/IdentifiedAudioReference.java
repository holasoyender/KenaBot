/*
 * File:          IdentifiedAudioReference.java
 * Path:          v3\src\main\java\player\IdentifiedAudioReference.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-29 12:59:00 +02:00
 *
 * SHA-256: 6e2a4545e70932fb2af7eae6bac3eae922502e5cc5b3c37c33bac3d8acf54ccc
 *
 */


package player;

import com.sedmelluq.discord.lavaplayer.tools.Units;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;

public class IdentifiedAudioReference extends AudioReference {

    private final String uri;

    public IdentifiedAudioReference(String identifier, String uri, String title) {
        super(identifier, title);
        this.uri = uri;
    }

    @Override
    public Long getLength() {
        return Units.CONTENT_LENGTH_UNKNOWN;
    }

    @Override
    public String getUri() {
        return uri;
    }
}
