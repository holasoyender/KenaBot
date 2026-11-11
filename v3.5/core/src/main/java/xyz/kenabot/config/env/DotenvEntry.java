/*
 * File:          DotenvEntry.java
 * Path:          v3.5\core\src\main\java\xyz\kenabot\config\env\DotenvEntry.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: e7a72f3840805d3eeaaa2a661350842d58ac0df0d4d53bc7e38c8535aed93398
 *
 */


package xyz.kenabot.config.env;

/**
 * A key value pair representing an environment variable and its value
 */
public record DotenvEntry(String key, String value) {

    /**
     * Creates a new dotenv entry using the provided key and value
     *
     * @param key   the dotenv entry name
     * @param value the dotenv entry value
     */
    public DotenvEntry {
    }

    /**
     * Returns the key for the {@link DotenvEntry}
     *
     * @return the key for the {@link DotenvEntry}
     */
    @Override
    public String key() {
        return key;
    }

    /**
     * Returns the value for the {@link DotenvEntry}
     *
     * @return the value for the {@link DotenvEntry}
     */
    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}