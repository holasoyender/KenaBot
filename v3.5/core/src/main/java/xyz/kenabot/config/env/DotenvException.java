/*
 * File:          DotenvException.java
 * Path:          v3.5\core\src\main\java\xyz\kenabot\config\env\DotenvException.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: 51fcbb3e0b795fb0822adf7fecd4e89dcc9c2981fe5f7a9666444196ed73054a
 *
 */


package xyz.kenabot.config.env;

public class DotenvException extends RuntimeException {

    public DotenvException(String message) {
        super(message);
    }

    public DotenvException(Throwable cause) {
        super(cause);
    }
}