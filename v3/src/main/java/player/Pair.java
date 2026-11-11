/*
 * File:          Pair.java
 * Path:          v3\src\main\java\player\Pair.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-29 12:59:00 +02:00
 *
 * SHA-256: 46b4512b7a901177ec0d8bee3d9e53d2dd0c6ec8c946f31192c3e4a1618d1cb5
 *
 */


package player;

public record Pair<L, R>(L left, R right) {
    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }
}
