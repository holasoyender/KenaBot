/*
 * File:          Dotenv.java
 * Path:          v3.5\core\src\main\java\xyz\kenabot\config\env\Dotenv.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: 3add56843685d51c55853832cb73e45f8b6ca01a16dd834673a24d9fa1597d18
 *
 */


package xyz.kenabot.config.env;

/**
 * Creates and configures a new Dotenv instance
 */
public interface Dotenv {

    /**
     * A dotenv entry filter
     */
    enum Filter {
        /**
         * Filter matching only environment variables declared in the .env file
         */
        DECLARED_IN_ENV_FILE
    }

    /**
     * Configures a new {@link Dotenv} instance
     * @return a new {@link Dotenv} instance
     */
    static DotenvBuilder configure() {
        return new DotenvBuilder();
    }

    /**
     * Creates and loads a {@link Dotenv} instance with default options
     * @return a new {@link Dotenv} instance
     */
    static Dotenv load() {
        return new DotenvBuilder().load();
    }

    /**
     * Retrieves the value of the environment variable specified by key
     * @param key the environment variable
     * @return the value of the environment variable
     */
    String get(String key);

    /**
     * Retrieves the value of the environment variable specified by key.
     * If the key does not exist, then the default value is returned
     * @param key the environment variable
     * @param defaultValue the default value to return
     * @return the value of the environment variable or default value
     */
    String get(String key, String defaultValue);
}