/*
 * File:          ThreadPools.java
 * Path:          v3.5\shared\src\main\java\xyz\kenabot\webhook\util\ThreadPools.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 1e3472bd53b2e70ce7ea4a876e5c222877e65ac186dfa47df5dea08ed723e652
 *
 */



package xyz.kenabot.webhook.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public class ThreadPools { // internal utils
    public static ScheduledExecutorService getDefaultPool(long id, ThreadFactory factory, boolean isDaemon) {
        return Executors.newSingleThreadScheduledExecutor(factory == null ? new DefaultWebhookThreadFactory(id, isDaemon) : factory);
    }

    public static final class DefaultWebhookThreadFactory implements ThreadFactory {
        private final long id;
        private final boolean isDaemon;

        public DefaultWebhookThreadFactory(long id, boolean isDaemon) {
            this.id = id;
            this.isDaemon = isDaemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            final Thread thread = new Thread(r, "Webhook-RateLimit Thread WebhookID: " + id);
            thread.setDaemon(isDaemon);
            return thread;
        }
    }
}
