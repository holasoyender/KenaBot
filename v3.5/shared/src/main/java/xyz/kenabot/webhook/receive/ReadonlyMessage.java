/*
 * File:          ReadonlyMessage.java
 * Path:          v3.5\shared\src\main\java\xyz\kenabot\webhook\receive\ReadonlyMessage.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 2cf1cb687b90b57238e307dae7c8f07dfda1aba2e26a8c5d0db1cd6b39e0907e
 *
 */



package xyz.kenabot.webhook.receive;

import xyz.kenabot.webhook.send.WebhookMessage;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.List;

/**
 * Readonly message representation used for responses
 * of {@link xyz.kenabot.webhook.WebhookClient} send methods.
 *
 * @see #toWebhookMessage()
 */
public class ReadonlyMessage implements JSONString {
    private final long id;
    private final long channelId;
    private final boolean mentionsEveryone;
    private final boolean tts;
    private final int flags;

    private final ReadonlyUser author;

    private final String content;
    private final List<ReadonlyEmbed> embeds;
    private final List<ReadonlyAttachment> attachments;

    private final List<ReadonlyUser> mentionedUsers;
    private final List<Long> mentionedRoles;

    public ReadonlyMessage(
            long id, long channelId, boolean mentionsEveryone, boolean tts, int flags,
            @NotNull ReadonlyUser author, @NotNull String content,
            @NotNull List<ReadonlyEmbed> embeds, @NotNull List<ReadonlyAttachment> attachments,
            @NotNull List<ReadonlyUser> mentionedUsers, @NotNull List<Long> mentionedRoles) {
        this.id = id;
        this.channelId = channelId;
        this.mentionsEveryone = mentionsEveryone;
        this.tts = tts;
        this.flags = flags;
        this.author = author;
        this.content = content;
        this.embeds = embeds;
        this.attachments = attachments;
        this.mentionedUsers = mentionedUsers;
        this.mentionedRoles = mentionedRoles;
    }

    /**
     * The id of this message.
     * <br>If this message is the beginning of a thread, then this is the thread id.
     *
     * @return The id
     */
    public long getId() {
        return id;
    }

    /**
     * The channel id for the channel this message was sent in
     *
     * @return The channel id
     */
    public long getChannelId() {
        return channelId;
    }

    /**
     * Whether this message mentioned everyone/here
     *
     * @return True, if this message mentioned everyone/here
     */
    public boolean isMentionsEveryone() {
        return mentionsEveryone;
    }

    /**
     * Whether this message used Text-to-Speech (TTS)
     *
     * @return True, if this message used TTS
     */
    public boolean isTTS() {
        return tts;
    }

    /**
     * The flags for this message.
     * <br>You can use {@link xyz.kenabot.webhook.MessageFlags} to determine which flags are set.
     *
     * @return The flags
     */
    public int getFlags() {
        return flags;
    }

    /**
     * The author of this message, represented by a {@link xyz.kenabot.webhook.receive.ReadonlyUser} instance.
     *
     * @return The author
     */
    @NotNull
    public ReadonlyUser getAuthor() {
        return author;
    }

    /**
     * The content of this message, this is displayed above embeds and attachments.
     *
     * @return The content
     */
    @NotNull
    public String getContent() {
        return content;
    }

    /**
     * The embeds in this message, a webhook can send up to 10 embeds
     * in one message. Additionally this contains embeds generated from links.
     *
     * @return List of embeds for this message
     */
    @NotNull
    public List<ReadonlyEmbed> getEmbeds() {
        return embeds;
    }

    /**
     * The attachments of this message. This contains files
     * added through methods such as {@link xyz.kenabot.webhook.send.WebhookMessageBuilder#addFile(java.io.File)}.
     * <br>The attachments only contain meta-data and not the actual files.
     *
     * @return List of attachments
     */
    @NotNull
    public List<ReadonlyAttachment> getAttachments() {
        return attachments;
    }

    /**
     * Users mentioned by this message.
     * <br>This will not contain all users when using an everyone/here mention,
     * it only contains directly mentioned users.
     *
     * @return List of mentioned users.
     */
    @NotNull
    public List<ReadonlyUser> getMentionedUsers() {
        return mentionedUsers;
    }

    /**
     * List of mentioned role ids
     *
     * @return List of ids for directly mentioned roles
     */
    @NotNull
    public List<Long> getMentionedRoles() {
        return mentionedRoles;
    }

    /**
     * Converts this message to a reduced webhook message.
     * <br>This can be used for sending.
     *
     * @return {@link xyz.kenabot.webhook.send.WebhookMessage}
     */
    @NotNull
    public WebhookMessage toWebhookMessage() {
        return WebhookMessage.from(this);
    }

    /**
     * JSON representation of this provider
     *
     * @return The JSON representation
     */
    @Override
    public String toString() {
        return toJSONString();
    }

    @Override
    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("content", content)
            .put("embeds", embeds)
            .put("mentions", mentionedUsers)
            .put("mention_roles", mentionedRoles)
            .put("attachments", attachments)
            .put("author", author)
            .put("tts", tts)
            .put("id", Long.toUnsignedString(id))
            .put("channel_id", Long.toUnsignedString(channelId))
            .put("mention_everyone", mentionsEveryone);
        return json.toString();
    }
}
