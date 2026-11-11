/*
 * File:          JdaAtenea.java
 * Path:          v3\src\main\java\atenea\driver\io\jda\JdaAtenea.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-07 01:11:56 +02:00
 *
 * SHA-256: 610c0f4c61851d3cd14709529b85228b800e9ffb02ab3ea86cb51c8f100d9459
 *
 */


package atenea.driver.io.jda;

import atenea.driver.AteneaUtil;
import atenea.driver.io.Atenea;
import com.mongodb.lang.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.session.SessionRecreateEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class JdaAtenea extends Atenea<JdaLink> implements EventListener {

    private static final Logger log = LoggerFactory.getLogger(JdaAtenea.class);

    /** JDA provider may be set at a later time */
    @Nullable
    private Function<Integer, JDA> jdaProvider;
    private boolean autoReconnect = true;
    private final JDAVoiceInterceptor voiceInterceptor;

    public JdaAtenea(String userId, int numShards, @Nullable Function<Integer, JDA> jdaProvider) {
        super(userId, numShards);
        this.jdaProvider = jdaProvider;
        this.voiceInterceptor = new JDAVoiceInterceptor(this);
    }

    /**
     * Creates a Atenea instance.
     * N.B: You must set the user ID before adding a node
     */
    public JdaAtenea(int numShards, @Nullable Function<Integer, JDA> jdaProvider) {
        this(null, numShards, jdaProvider);
    }

    @SuppressWarnings("unused")
    public JdaAtenea(int numShards) {
        this(numShards, null);
    }

    @SuppressWarnings("unused")
    public boolean getAutoReconnect() {
        return autoReconnect;
    }

    @SuppressWarnings("unused")
    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public JdaLink getLink(Guild guild) {
        return getLink(guild.getId());
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    @Nullable
    public JdaLink getExistingLink(Guild guild) {
        return getExistingLink(guild.getId());
    }

    /**
     * Returns the JDA instance with the {@code shardId shard ID}
     *
     * @param shardId the ID of the shard
     * @return the JDA instance with the specified shard ID
     *
     * @throws IllegalStateException if the JDA provider has not been initialised
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    @NonNull
    public JDA getJda(int shardId) {
        if (jdaProvider == null) throw new IllegalStateException("JDAProvider is not initialised!");

        JDA result = jdaProvider.apply(shardId);
        if (result == null) throw new IllegalStateException("JDAProvider returned null for shard " + shardId);

        return jdaProvider.apply(shardId);
    }

    /**
     * Returns the JDA instance with the {@code snowflake snowflake}
     *
     * @param snowflake the snowflake of the shard
     * @return the JDA instance with the specified snowflake
     *
     * @throws IllegalStateException if the JDA provider has not been initialised
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public JDA getJdaFromSnowflake(String snowflake) {
        return getJda(AteneaUtil.getShardFromSnowflake(snowflake, numShards));
    }

    @SuppressWarnings("unused")
    public void setJdaProvider(@Nullable Function<Integer, JDA> jdaProvider) {
        this.jdaProvider = jdaProvider;
    }

    @SuppressWarnings("unused")
    @NonNull
    public JDAVoiceInterceptor getVoiceInterceptor() {
        return voiceInterceptor;
    }

    @Override
    public void onEvent(@NonNull GenericEvent event) {
        if (event instanceof SessionRecreateEvent) {
            if (autoReconnect) {
                getLinksMap().forEach((guildId, link) -> {
                    try {
                        //Note: We also ensure that the link belongs to the JDA object
                        if (link.getLastChannel() != null
                                && event.getJDA().getGuildById(guildId) != null) {
                            link.connect(event.getJDA().getVoiceChannelById(link.getLastChannel()), false);
                        }
                    } catch (Exception e) {
                        log.error("Caught exception while trying to reconnect link " + link, e);
                    }
                });
            }
        } else if (event instanceof GuildLeaveEvent) {
            JdaLink link = getLinksMap().get(((GuildLeaveEvent) event).getGuild().getId());
            if (link == null) return;

            link.removeConnection();
        } else if (event instanceof ChannelDeleteEvent e) {
            JdaLink link = getLinksMap().get(e.getGuild().getId());
            if (link == null || !e.getChannel().getId().equals(link.getLastChannel())) return;

            link.removeConnection();
        }
    }

    @Override
    protected JdaLink buildNewLink(String guildId) {
        return new JdaLink(this, guildId);
    }
}
