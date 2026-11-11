/*
 * File:          JDAVoiceInterceptor.java
 * Path:          v3\src\main\java\atenea\driver\io\jda\JDAVoiceInterceptor.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-14 18:13:13 +02:00
 *
 * SHA-256: 959ae1aa5bd54393977301b1a0864c59d19727af90657dfe03c85234e9907f85
 *
 */


package atenea.driver.io.jda;

import atenea.driver.io.Link;
import metrics.Regions;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import org.json.JSONObject;

import java.util.Objects;

/**
 * You have to set this class on the JDABuilder or DefaultShardManagerBuilder
 */
public class JDAVoiceInterceptor implements VoiceDispatchInterceptor {

    private final JdaAtenea atenea;

    public JDAVoiceInterceptor(JdaAtenea atenea) {
        this.atenea = atenea;
    }

    @Override
    public void onVoiceServerUpdate(VoiceServerUpdate update) {
        JSONObject content = new JSONObject(update.toData().getObject("d").toMap());

        // Get session
        Guild guild = update.getGuild();

        Regions.INSTANCE.onMessage(content);
        atenea.getLink(guild).onVoiceServerUpdate(content, Objects.requireNonNull(guild.getSelfMember().getVoiceState()).getSessionId());
    }

    @Override
    public boolean onVoiceStateUpdate(VoiceStateUpdate update) {

        AudioChannel channel = update.getChannel();
        JdaLink link = atenea.getLink(update.getGuildId());

        if (channel == null) {
            // Null channel means disconnected
            if (link.getState() != Link.State.DESTROYED) {
                link.onDisconnected();
            }
        } else {
            link.setChannel(channel.getId()); // Change expected channel
        }

        return link.getState() == Link.State.CONNECTED;
    }
}
