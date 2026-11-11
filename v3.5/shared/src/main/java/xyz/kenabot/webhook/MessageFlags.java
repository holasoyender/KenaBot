/*
 * File:          MessageFlags.java
 * Path:          v3.5\shared\src\main\java\xyz\kenabot\webhook\MessageFlags.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 8161aa2e7b7a9a4d7841eb84a7825af03284707dffd965b5af88e6532470a44d
 *
 */



package xyz.kenabot.webhook;

/**
 * Constants for the message flags described by the <a href="https://discord.com/developers/docs/resources/channel#message-object-message-flags" target="_blank">Discord Documentation</a>.
 */
@SuppressWarnings("PointlessBitwiseExpression")
public class MessageFlags {
    public static final int CROSSPOSTED            = 1 << 0;
    public static final int IS_CROSSPOSTED         = 1 << 1;
    public static final int SUPPRESS_EMBEDS        = 1 << 2;
    public static final int SOURCE_MESSAGE_DELETED = 1 << 3;
    public static final int URGENT                 = 1 << 4;
    public static final int HAS_THREAD             = 1 << 5;
    public static final int EPHEMERAL              = 1 << 6;
    public static final int LOADING                = 1 << 7;
}
