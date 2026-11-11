/*
 * File:          ReadonlyUser.java
 * Path:          v3.5\shared\src\main\java\xyz\kenabot\webhook\receive\ReadonlyUser.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 85a1f127e251812feacd1f5cce4b88579f735b063f84b7aa5655c1fcef6d8e79
 *
 */



package xyz.kenabot.webhook.receive;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONPropertyName;
import org.json.JSONString;

import java.util.Locale;

/**
 * Readonly POJO of a discord user
 */
public class ReadonlyUser implements JSONString {
    private final long id;
    private final short discriminator;
    private final boolean bot;
    private final String name;
    private final String avatar;

    public ReadonlyUser(long id, short discriminator, boolean bot, @NotNull String name, @Nullable String avatar) {
        this.id = id;
        this.discriminator = discriminator;
        this.bot = bot;
        this.name = name;
        this.avatar = avatar;
    }

    /**
     * The id of this user
     *
     * @return The id
     */
    public long getId() {
        return id;
    }

    /**
     * The 4 digit discriminator of this user
     * <br>This is show in the client after the {@code #} when viewing profiles.
     *
     * @return The discriminator
     */
    public String getDiscriminator() {
        return String.format(Locale.ROOT, "%04d", discriminator);
    }

    /**
     * Whether this is a bot or not, webhook authors are always bots.
     *
     * @return True, if this is a bot
     */
    public boolean isBot() {
        return bot;
    }

    /**
     * The name of this user, this is the username and not the guild-specific nickname.
     *
     * @return The name of this user
     */
    @NotNull
    @JSONPropertyName("username")
    public String getName() {
        return name;
    }

    /**
     * The avatar id of this user, or {@code null} if no avatar is set.
     *
     * @return The avatar id
     */
    @Nullable
    @JSONPropertyName("avatar_id")
    public String getAvatarId() {
        return avatar;
    }

    /**
     * JSON representation of this user
     *
     * @return THe JSON representation of this user
     */
    @Override
    public String toString() {
        return toJSONString();
    }

    @Override
    public String toJSONString() {
        return new JSONObject(this).toString();
    }
}
