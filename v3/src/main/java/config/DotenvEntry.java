/*
 * File:          DotenvEntry.java
 * Path:          v3\src\main\java\config\DotenvEntry.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 3fcdc8f733a75ba679a86b4c371224560e0bbf92728c0cc227b13eb6e5f2db65
 *
 */


package config;


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