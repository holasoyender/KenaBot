/*
 * File:          LibraryInfo.java
 * Path:          v3.5\shared\src\main\java\xyz\kenabot\webhook\LibraryInfo.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 89446f9b5be221278670d10e5aaab17c66756682dca3793c1a255de488989ecf
 *
 */



package xyz.kenabot.webhook;

public class LibraryInfo {
    public static final int DISCORD_API_VERSION = 9;
    public static final String VERSION_MAJOR = "@MAJOR@";
    public static final String VERSION_MINOR = "@MINOR@";
    public static final String VERSION_PATCH = "@PATCH@";
    public static final String VERSION = "@VERSION@";
    public static final String COMMIT = "@COMMIT@";

    public static final String DEBUG_INFO = "DISCORD_API_VERSION: " + DISCORD_API_VERSION +
                                            "\nVERSION: " + VERSION +
                                            "\nCOMMIT: " + COMMIT;
}
