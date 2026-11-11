/*
 * File:          HttpException.java
 * Path:          v3.5\shared\src\main\java\xyz\kenabot\webhook\exception\HttpException.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 62b1176276b5c08ea4e6fd91c40ed7c02549c169c73f2b8662e16a64aec96f11
 *
 */



package xyz.kenabot.webhook.exception;

import okhttp3.Headers;
import org.jetbrains.annotations.NotNull;


/**
 * Exception thrown in case of unexpected non-2xx HTTP response.
 */
public class HttpException extends RuntimeException {

    private final int code;
    private final String body;
    private final Headers headers;

    public HttpException(int code, @NotNull String body, @NotNull Headers headers) {
        super("Request returned failure " + code + ": " + body);
        this.body = body;
        this.code = code;
        this.headers = headers;
    }

    /**
     * The HTTP status code
     *
     * @return The status code
     */
    public int getCode() {
        return code;
    }

    /**
     * The body of HTTP response
     *
     * @return The body
     */
    @NotNull
    public String getBody() {
        return body;
    }

    /**
     * The HTTP headers. Useful to check content-type or rate limit buckets.
     *
     * @return {@link okhttp3.Headers}
     */
    @NotNull
    public Headers getHeaders() {
        return headers;
    }
}
