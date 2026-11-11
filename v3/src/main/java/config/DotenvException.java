/*
 * File:          DotenvException.java
 * Path:          v3\src\main\java\config\DotenvException.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: e15fa0b4b88e13cd6e36add2c426bbcc7a4b8a193eb2e349cf6791565ffb89ce
 *
 */


package config;

public class DotenvException extends RuntimeException {

    public DotenvException(String message) {
        super(message);
    }

    public DotenvException(Throwable cause) {
        super(cause);
    }
}