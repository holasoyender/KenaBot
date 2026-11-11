/*
 * File:          WebhookClientBuilder.java
 * Path:          v3.5\shared\src\main\java\xyz\kenabot\webhook\WebhookClientBuilder.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: f36501ec3091230aa1373fd814b9e86819cc3517e77c66896567ec13b4d015cb
 *
 */



package xyz.kenabot.webhook;

import xyz.kenabot.webhook.send.AllowedMentions;
import xyz.kenabot.webhook.util.ThreadPools;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builder for a {@link xyz.kenabot.webhook.WebhookClient} instance.
 *
 * @see xyz.kenabot.webhook.WebhookClient#withId(long, String)
 * @see xyz.kenabot.webhook.WebhookClient#withUrl(String)
 */
public class WebhookClientBuilder { //TODO: tests
    /**
     * Pattern used to validate webhook urls
     * {@code (?:https?://)?(?:\w+\.)?discord(?:app)?\.com/api(?:/v\d+)?/webhooks/(\d+)/([\w-]+)(?:/(?:\w+)?)?}
     */
    public static final Pattern WEBHOOK_PATTERN = Pattern.compile("(?:https?://)?(?:\\w+\\.)?discord(?:app)?\\.com/api(?:/v\\d+)?/webhooks/(\\d+)/([\\w-]+)(?:/(?:\\w+)?)?");

    protected final long id;
    protected final String token;
    protected long threadId;
    protected ScheduledExecutorService pool;
    protected OkHttpClient client;
    protected ThreadFactory threadFactory;
    protected AllowedMentions allowedMentions = AllowedMentions.all();
    protected boolean isDaemon;
    protected boolean parseMessage = true;

    /**
     * Creates a new WebhookClientBuilder for the specified webhook components
     *
     * @param  id
     *         The webhook id
     * @param  token
     *         The webhook token
     *
     * @throws java.lang.NullPointerException
     *         If the token is null
     */
    public WebhookClientBuilder(final long id, @NotNull final String token) {
        Objects.requireNonNull(token, "Token");
        this.id = id;
        this.token = token;
    }

    /**
     * Creates a new WebhookClientBuilder for the specified webhook url
     * <br>The url is verified using {@link #WEBHOOK_PATTERN}.
     *
     * @param  url
     *         The url to use
     *
     * @throws java.lang.NullPointerException
     *         If the url is null
     * @throws java.lang.IllegalArgumentException
     *         If the url is not valid
     */
    public WebhookClientBuilder(@NotNull String url) {
        Objects.requireNonNull(url, "Url");
        Matcher matcher = WEBHOOK_PATTERN.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Failed to parse webhook URL");
        }

        this.id = Long.parseUnsignedLong(matcher.group(1));
        this.token = matcher.group(2);
    }

    /////////////////////////////////
    /// Third-party compatibility ///
    /////////////////////////////////

    /**
     * Creates a WebhookClientBuilder for the provided webhook.
     *
     * @param  webhook
     *         The webhook
     *
     * @throws NullPointerException
     *         If the webhook is null or does not provide a token
     *
     * @return The WebhookClientBuilder
     */
    @NotNull
    public static WebhookClientBuilder fromJDA(@NotNull net.dv8tion.jda.api.entities.Webhook webhook) {
        Objects.requireNonNull(webhook, "Webhook");
        return new WebhookClientBuilder(webhook.getIdLong(), Objects.requireNonNull(webhook.getToken(), "Webhook Token"));
    }


    /**
     * The {@link java.util.concurrent.ScheduledExecutorService} that is used to execute
     * send requests in the resulting {@link xyz.kenabot.webhook.WebhookClient}.
     * <br>This will be closed by a call to {@link WebhookClient#close()}.
     *
     * @param  executorService
     *         The executor service to use
     *
     * @return The current builder, for chaining convenience
     */
    @NotNull
    public WebhookClientBuilder setExecutorService(@Nullable ScheduledExecutorService executorService) {
        this.pool = executorService;
        return this;
    }

    /**
     * The {@link okhttp3.OkHttpClient} that is used to execute
     * send requests in the resulting {@link xyz.kenabot.webhook.WebhookClient}.
     * <br>It is usually not necessary to use multiple different clients in one application
     *
     * @param  client
     *         The http client to use
     *
     * @return The current builder, for chaining convenience
     */
    @NotNull
    public WebhookClientBuilder setHttpClient(@Nullable OkHttpClient client) {
        this.client = client;
        return this;
    }

    /**
     * The {@link java.util.concurrent.ThreadFactory} that is used to initialize
     * the default {@link java.util.concurrent.ScheduledExecutorService} used if
     * {@link #setExecutorService(java.util.concurrent.ScheduledExecutorService)} is not configured.
     *
     * @param  factory
     *         The factory to use
     *
     * @return The current builder, for chaining convenience
     */
    @NotNull
    public WebhookClientBuilder setThreadFactory(@Nullable ThreadFactory factory) {
        this.threadFactory = factory;
        return this;
    }

    /**
     * The default mention whitelist for every outgoing message.
     * <br>See {@link AllowedMentions} for more details.
     *
     * @param  mentions
     *         The mention whitelist
     *
     * @return This builder for chaining convenience
     */
    @NotNull
    public WebhookClientBuilder setAllowedMentions(@Nullable AllowedMentions mentions) {
        this.allowedMentions = mentions == null ? AllowedMentions.all() : mentions;
        return this;
    }

    /**
     * Whether the default executor should use daemon threads.
     * <br>This has no effect if either {@link #setExecutorService(java.util.concurrent.ScheduledExecutorService)}
     * or {@link #setThreadFactory(java.util.concurrent.ThreadFactory)} are configured to non-null values.
     *
     * @param  isDaemon
     *         Whether to use daemon threads or not
     *
     * @return The current builder, for chaining convenience
     */
    @NotNull
    public WebhookClientBuilder setDaemon(boolean isDaemon) {
        this.isDaemon = isDaemon;
        return this;
    }

    /**
     * Whether resulting messages should be parsed after sending,
     * if this is set to {@code false} the futures returned by {@link xyz.kenabot.webhook.WebhookClient}
     * will receive {@code null} instead of instances of {@link xyz.kenabot.webhook.receive.ReadonlyMessage}.
     *
     * @param  waitForMessage
     *         True, if the client should parse resulting messages (default behavior)
     *
     * @return The current builder, for chaining convenience
     */
    @NotNull
    public WebhookClientBuilder setWait(boolean waitForMessage) {
        this.parseMessage = waitForMessage;
        return this;
    }

    /**
     * The ID for the thread you want the messages to be posted to.
     * <br>You can use {@link WebhookClient#onThread(long)} to send specific messages to threads.
     *
     * @param  threadId
     *         The target thread id, or 0 to not use threads
     *
     * @return The current builder, for chaining convenience
     */
    @NotNull
    public WebhookClientBuilder setThreadId(long threadId) {
        this.threadId = threadId;
        return this;
    }

    /**
     * Builds the {@link xyz.kenabot.webhook.WebhookClient}
     * with the current settings
     *
     * @return {@link xyz.kenabot.webhook.WebhookClient} instance
     */
    @NotNull
    public WebhookClient build() {
        OkHttpClient client = this.client == null ? new OkHttpClient() : this.client;
        ScheduledExecutorService pool = this.pool != null ? this.pool : ThreadPools.getDefaultPool(id, threadFactory, isDaemon);
        return new WebhookClient(id, token, parseMessage, client, pool, allowedMentions, threadId);
    }

    /**
     * Builds the {@link JDAWebhookClient}
     * with the current settings
     *
     * @return {@link JDAWebhookClient} instance
     */
    @NotNull
    public JDAWebhookClient buildJDA() {
        OkHttpClient client = this.client == null ? new OkHttpClient() : this.client;
        ScheduledExecutorService pool = this.pool != null ? this.pool : ThreadPools.getDefaultPool(id, threadFactory, isDaemon);
        return new JDAWebhookClient(id, token, parseMessage, client, pool, allowedMentions, threadId);
    }
}
