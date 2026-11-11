/*
 * File:          TrackData.java
 * Path:          v3\src\main\java\atenea\driver\player\TrackData.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: 912709cd0c3af3a70e438d992ae4a2011802c2a2e28cc690908aa00bdea58cbd
 *
 */


package atenea.driver.player;
public class TrackData {

    public final long startPos;
    public final long endPos;

    public TrackData(long startPos, long endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }
}
