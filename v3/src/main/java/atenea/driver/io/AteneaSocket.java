/*
 * File:          AteneaSocket.java
 * Path:          v3\src\main\java\atenea\driver\io\AteneaSocket.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-16 12:25:04 +02:00
 *
 * SHA-256: 0fcd0e004fbf5ee2b83d6e04dd55f3a6a0318c9f5372f8c6489d6ef911185af7
 *
 */


package atenea.driver.io;

import com.mongodb.lang.NonNull;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import atenea.driver.AteneaUtil;
import atenea.driver.player.AteneaPlayer;
import atenea.driver.player.event.*;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.nio.channels.NotYetConnectedException;
import java.util.Map;
import java.util.Objects;

public class AteneaSocket extends ReusableWebSocket {

    private static final Logger log = LoggerFactory.getLogger(AteneaSocket.class);

    private static final int TIMEOUT_MS = 5000;
    @NonNull
    private final String name;
    @NonNull
    private final String password;
    @NonNull
    private final Atenea<?> atenea;
    @Nullable
    private RemoteStats stats;
    long lastReconnectAttempt = 0;
    private int reconnectsAttempted = 0;
    @NonNull
    private final URI remoteUri;
    private final AteneaRestClient restClient;
    private boolean available = false;

    AteneaSocket(@NonNull String name, @NonNull Atenea<?> atenea, @NonNull URI serverUri, Draft protocolDraft, Map<String, String> headers) {
        super(serverUri, protocolDraft, headers, TIMEOUT_MS);
        this.name = name;
        this.password = headers.get("Authorization");
        this.atenea = atenea;
        this.remoteUri = serverUri;
        this.restClient = new AteneaRestClient(this);
    }

    @NonNull
    public AteneaRestClient getRestClient() {
        return restClient;
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        log.info("Received handshake from server {}", remoteUri);
        available = true;
        atenea.loadBalancer.onNodeConnect(this);
        reconnectsAttempted = 0;

        boolean resumed = handshakeData.getFieldValue("Session-Resumed").equals("true");
        if (resumed) {
            log.info("Session Resumed to {}", remoteUri);
        }
    }

    @Override
    public void onMessage(String message) {
        JSONObject json = new JSONObject(message);

        if (!Objects.equals(json.getString("op"), "playerUpdate")) {
            log.debug(message);
        }

        switch (json.getString("op")) {
            case "playerUpdate" -> atenea.getLink(json.getString("guildId"))
                    .getPlayer()
                    .provideState(json.getJSONObject("state"));
            case "stats" -> stats = new RemoteStats(json);
            case "event" -> {
                try {
                    handleEvent(json);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case "ready" -> log.info("Socket is ready");
            default -> log.warn("Unexpected operation: {}", json.getString("op"));
        }
    }

    /**
     * Implementation details:
     * The only events extending {@link atenea.driver.player.event.PlayerEvent} produced by the remote server are these:
     * 1. TrackEndEvent
     * 2. TrackExceptionEvent
     * 3. TrackStuckEvent
     * 4. WebSocketClosedEvent
     */
    private void handleEvent(JSONObject json) throws IOException {
        Link link = atenea.getLink(json.getString("guildId"));
        AteneaPlayer player = atenea.getLink(json.getString("guildId")).getPlayer();
        PlayerEvent event = null;

        switch (json.getString("type")) {
            case "TrackStartEvent":
                break;
            case "TrackEndEvent":
                event = new TrackEndEvent(player,
                        AteneaUtil.toAudioTrackWithData(player, json.getString("track")),
                        AudioTrackEndReason.valueOf(json.getString("reason"))
                );
                break;
            case "TrackExceptionEvent":
                Exception ex;
                if (json.has("exception")) {
                    JSONObject jsonEx = json.getJSONObject("exception");
                    ex = new FriendlyException(
                            jsonEx.getString("message"),
                            FriendlyException.Severity.valueOf(jsonEx.getString("severity")),
                            new RuntimeException(jsonEx.getString("cause"))
                    );
                } else {
                    ex = new RemoteTrackException(json.getString("error"));
                }

                event = new TrackExceptionEvent(player,
                        AteneaUtil.toAudioTrackWithData(player, json.getString("track")), ex
                );
                break;
            case "TrackStuckEvent":
                event = new TrackStuckEvent(player,
                        AteneaUtil.toAudioTrackWithData(player, json.getString("track")),
                        json.getLong("thresholdMs")
                );
                break;
            case "WebSocketClosedEvent":
                // Unlike the other events, this is handled by the Link instead of the AteneaPlayer,
                // as this event is more relevant to the implementation of Link.
                link.onVoiceWebSocketClosed(
                        json.getInt("code"),
                        json.getString("reason"),
                        json.getBoolean("byRemote")
                );
                // pass the event to user
                event = new WebSocketClosedEvent(player, json.getInt("code"), json.getString("reason"), json.getBoolean("byRemote"));
                break;
            default:
                log.warn("Unexpected event type: {}", json.getString("type"));
                log.debug("Unexpected Event: {}", json);
                break;
        }

        if (event != null) player.emitEvent(event);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        available = false;
        reason = reason == null ? "<no reason given>" : reason;
        if (code == 1000) {
            log.info("Connection to {} closed gracefully with reason: {} :: Remote={}", getRemoteUri(), reason,remote);
        } else {
            log.warn("Connection to {} closed unexpectedly with reason {}: {} :: Remote={}", getRemoteUri(), code, reason, remote);
        }
        atenea.loadBalancer.onNodeDisconnect(this);
    }

    @Override
    public void onError(Exception ex) {
        if (ex instanceof ConnectException) {
            log.warn("Failed to connect to {}, retrying in {} seconds.", getRemoteUri(), getReconnectInterval()/1000);
            return;
        }

        log.error("Caught exception in websocket", ex);
    }

    @Override
    public void send(String text) throws NotYetConnectedException {
        // Note: If we lose connection we will reconnect and initialize properly
        if (isOpen()) {
            log.trace("Sending ->" + text);
            super.send(text);
        } else if (isConnecting()) {
            log.warn("Attempting to send messages to {} WHILE connecting. Ignoring.", getRemoteUri());
        }
    }

    public void configureResuming(String resumeKey) {
        configureResuming(resumeKey, -1);
    }

    public void configureResuming(String resumeKey, long timeout) {
        JSONObject json = new JSONObject()
                .put("op", "configureResuming")
                .put("key", resumeKey);
        if (timeout > 0) {
            json.put("timeout", timeout);
        }
        send(json.toString());
    }

    @NonNull
    @SuppressWarnings("unused")
    public URI getRemoteUri() {
        return remoteUri;
    }

    void attemptReconnect() {
        lastReconnectAttempt = System.currentTimeMillis();
        reconnectsAttempted++;
        connect();
    }

    long getReconnectInterval() {
        return reconnectsAttempted * 2000L - 2000;
    }

    @Nullable
    public RemoteStats getStats() {
        return stats;
    }

    public boolean isAvailable() {
        return available && isOpen() && !isClosing();
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "AteneaSocket{" +
                "name=" + name +
                ",remoteUri=" + remoteUri +
                '}';
    }
}
