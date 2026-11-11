/*
 * File:          Link.java
 * Path:          v3\src\main\java\atenea\driver\io\Link.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: 77bbf8a4fdf67351b2deae07c7d035238977eb7f7a9fefd3e2b77a29300d3197
 *
 */


package atenea.driver.io;

import atenea.driver.player.AteneaPlayer;
import com.mongodb.lang.NonNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Indicates which node we are linked to, what voice channel to use, and what player we are using
 */
abstract public class Link {

    private static final Logger log = LoggerFactory.getLogger(Link.class);
    private JSONObject lastVoiceServerUpdate = null;
    private String lastSessionId = null;
    private final Atenea<?> atenea;
    protected final long guild;
    private AteneaPlayer player;
    private volatile String channel = null;
    private volatile AteneaSocket node = null;
    /* May only be set by setState() */
    private volatile State state = State.NOT_CONNECTED;

    protected Link(Atenea<?> atenea, String guildId) {
        this.atenea = atenea;
        this.guild = Long.parseLong(guildId);
    }

    public AteneaPlayer getPlayer() {
        if (player == null) {
            player = new AteneaPlayer(this);
        }

        return player;
    }

    public Atenea<?> getAtenea() {
        return atenea;
    }

    public AteneaRestClient getRestClient() {
        final AteneaSocket node = getNode(true);
        if (node == null) throw new IllegalStateException("No available nodes!");

        return node.getRestClient();
    }

    @SuppressWarnings("unused")
    public void resetPlayer() {
        player = null;
    }

    public String getGuildId() {
        return Long.toString(guild);
    }

    public long getGuildIdLong() {
        return guild;
    }

    /**
     * @deprecated may cause unexpected reconnects and other strange behavior. Use {@link #destroy()} instead.
     * Will be removed if we change the lifecycle of this class.
     */
    @Deprecated
    public void disconnect() {
        setState(State.DISCONNECTING);
        queueAudioDisconnect();
    }

    public void changeNode(AteneaSocket newNode) {
        node = newNode;
        if (lastVoiceServerUpdate != null) {
            onVoiceServerUpdate(getLastVoiceServerUpdate(), lastSessionId);
            player.onNodeChange();
        }
    }

    /**
     * Invoked when we get a voice state update telling us that we have disconnected.
     */
    public void onDisconnected() {
        setState(State.NOT_CONNECTED);
        AteneaSocket socket = getNode(false);
        if (socket != null && state != State.DESTROYING && state != State.DESTROYED) {
            socket.send(new JSONObject()
                    .put("op", "destroy")
                    .put("guildId", Long.toString(guild))
                    .toString());
            node = null;
        }
    }

    /**
     * Disconnects the voice connection (if any) and internally dereferences this {@link Link}.
     * <p>
     * You should invoke this method your bot leaves a guild.
     */
    @SuppressWarnings("unused")
    public void destroy() {
        boolean shouldDisconnect = state != State.DISCONNECTING && state != State.NOT_CONNECTED;
        setState(State.DESTROYING);
        if (shouldDisconnect) {
            try {
                queueAudioDisconnect();
            } catch (RuntimeException ignored) {
                // This could fail in case we are not in a guild.
                // In that case, we are already disconnected
            }
        }
        setState(State.DESTROYED);
        atenea.removeDestroyedLink(this);
        AteneaSocket socket = getNode(false);
        if (socket != null) {
            socket.send(new JSONObject()
                    .put("op", "destroy")
                    .put("guildId", Long.toString(guild))
                    .toString());
        }
    }

    protected abstract void removeConnection();
    protected abstract void queueAudioDisconnect();
    protected abstract void queueAudioConnect(long channelId);

    /**
     * @return The current node
     */
    @Nullable
    @SuppressWarnings({"WeakerAccess", "unused"})
    public AteneaSocket getNode() {
        return getNode(false);
    }

    /**
     * @param selectIfAbsent If true determines a new socket if there isn't one yet
     * @return The current node
     */
    @Nullable
    @SuppressWarnings("WeakerAccess")
    public AteneaSocket getNode(boolean selectIfAbsent) {
        if (selectIfAbsent && node == null) {
            node = atenea.loadBalancer.determineBestSocket(guild);
            if (player != null) player.onNodeChange();
        }
        return node;
    }

    /**
     * @return The channel we are currently connect to
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    @Nullable
    public String getChannel() {
        if (channel == null || state == State.DESTROYED || state == State.NOT_CONNECTED) return null;

        return channel;
    }

    /**
     * @return The channel we are currently connected to, or which we were connected to
     */
    @Nullable
    public String getLastChannel() {
        return channel;
    }

    /**
     * @return The {@link State} of this {@link Link}
     */
    @SuppressWarnings("unused")
    public State getState() {
        return state;
    }

    public void setState(@NonNull State state) {
        if (this.state == State.DESTROYED && state != State.DESTROYED)
            throw new IllegalStateException("Cannot change state to " + state + " when state is " + State.DESTROYED);
        if (this.state == State.DESTROYING && state != State.DESTROYED) {
            throw new IllegalStateException("Cannot change state to " + state + " when state is " + State.DESTROYING);
        }
        log.debug("Link {} changed state from {} to {}", this, this.state, state);
        this.state = state;
    }

    /**
     * Invoked when we receive a voice state update from Discord, which tells us we have joined a channel
     */

    public void setChannel(@NonNull String channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "Link{" +
                "guild='" + guild + '\'' +
                ", channel='" + channel + '\'' +
                ", state=" + state +
                '}';
    }

    public void onVoiceServerUpdate(JSONObject json, String sessionId) {
        lastVoiceServerUpdate = json;
        lastSessionId = sessionId;

        // Send WS message
        JSONObject out = new JSONObject();
        out.put("op", "voiceUpdate");
        out.put("sessionId", sessionId);
        out.put("guildId", Long.toString(guild));
        out.put("event", lastVoiceServerUpdate);

        //noinspection ConstantConditions
        getNode(true).send(out.toString());
        setState(Link.State.CONNECTED);
    }

    public JSONObject getLastVoiceServerUpdate() {
        return lastVoiceServerUpdate;
    }

    /**
     * Invoked when the remote Atenea server reports that this Link's WebSocket to the voice server was closed.
     * This could be because of an expired voice session, that might have to be renewed.
     *
     * @param code the RFC 6455 close code.
     * @param reason the reason for closure, provided by the closing peer.
     * @param byRemote true if closed by Discord, false if closed by the Atenea server.
     */
    @SuppressWarnings("unused")
    public void onVoiceWebSocketClosed(int code, String reason, boolean byRemote) {}

    public enum State {
        /**
         * Default, means we are not trying to use voice at all
         */
        NOT_CONNECTED,

        /**
         * Waiting for VOICE_SERVER_UPDATE
         */
        CONNECTING,

        /**
         * We have dispatched the voice server info to the server, and it should (soon) be connected.
         */
        CONNECTED,

        /**
         * Waiting for confirmation from Discord that we have connected
         */
        DISCONNECTING,

        /**
         * This {@link Link} is being destroyed
         */
        DESTROYING,

        /**
         * This {@link Link} has been destroyed and will soon (if not already) be unmapped from {@link Atenea}
         */
        DESTROYED
    }

}
