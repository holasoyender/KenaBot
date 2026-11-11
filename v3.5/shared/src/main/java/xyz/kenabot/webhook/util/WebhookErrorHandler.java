/*
 * File:          WebhookErrorHandler.java
 * Path:          v3.5\shared\src\main\java\xyz\kenabot\webhook\util\WebhookErrorHandler.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 565e5fd767ed7073ca3f26e7510610c507d5d87de71a845aafbf7e1e64f616f1
 *
 */



package xyz.kenabot.webhook.util;

import xyz.kenabot.webhook.WebhookClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

/**
 * Used to dynamically handle errors for webhook requests in {@link WebhookClient}
 * <br>If not explicitly configured, this uses {@link #DEFAULT}.
 *
 * @see WebhookClient#setDefaultErrorHandler(WebhookErrorHandler)
 * @see WebhookClient#setErrorHandler(WebhookErrorHandler)
 */
@FunctionalInterface
public interface WebhookErrorHandler {
    /**
     * The default error handling which simply logs the exception using SLF4J
     */
    WebhookErrorHandler DEFAULT = (client, message, throwable) -> LoggerFactory.getLogger(WebhookClient.class).error(message, throwable);

    /**
     * Implements error handling, must not throw anything!
     *
     * @param client
     *        The {@link WebhookClient} instance which encountered the exception
     * @param message
     *        The context message used for logging
     * @param throwable
     *        The encountered exception, or null if the error is only a context message
     */
    void handle(@NotNull WebhookClient client, @NotNull String message, @Nullable Throwable throwable);
}
