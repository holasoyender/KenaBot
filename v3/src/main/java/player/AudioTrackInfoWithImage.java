/*
 * File:          AudioTrackInfoWithImage.java
 * Path:          v3\src\main\java\player\AudioTrackInfoWithImage.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-29 12:59:00 +02:00
 *
 * SHA-256: 75a8b2dca1b89a3d33791699e2bc71a81c58aeafb441839059a3c407b3a17b53
 *
 */


package player;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

public class AudioTrackInfoWithImage extends AudioTrackInfo {

    public AudioTrackInfoWithImage(String title, String author, long length, String identifier, boolean isStream, String uri) {
        super(title, author, length, identifier, isStream, uri);
    }
}
