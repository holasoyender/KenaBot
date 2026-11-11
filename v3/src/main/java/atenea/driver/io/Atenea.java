/*
 * File:          Atenea.java
 * Path:          v3\src\main\java\atenea\driver\io\Atenea.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-14 18:48:17 +02:00
 *
 * SHA-256: 8ffebf0ae7ffa50b9f841140373f99b8b968d1f498eb567b673dbf98eadef2f1
 *
 */


package atenea.driver.io;

import com.mongodb.lang.NonNull;
import org.java_websocket.drafts.Draft_6455;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class Atenea<T extends Link> {

    private static final Logger log = LoggerFactory.getLogger(Atenea.class);

    @SuppressWarnings("WeakerAccess")
    protected int numShards;
    /** User id may be set at a later time */
    @Nullable
    private String userId;
    private final ConcurrentHashMap<String, T> links = new ConcurrentHashMap<>();
    final List<AteneaSocket> nodes = new CopyOnWriteArrayList<>();
    final AteneaLoadBalancer loadBalancer = new AteneaLoadBalancer(this);

    private final ScheduledExecutorService reconnectService;

    public Atenea(@Nullable String userId, int numShards) {
        this.userId = userId;
        this.numShards = numShards;

        reconnectService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "atenea-reconnect-thread");
            thread.setDaemon(true);
            return thread;
        });
        reconnectService.scheduleWithFixedDelay(new ReconnectTask(this), 0, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Creates a Atenea instance.
     * N.B: You must set the user ID before adding a node
     */
    @SuppressWarnings("unused")
    public Atenea(int numShards) {
        this(null, numShards);
    }

    public AteneaSocket addNode(@NonNull String name, @NonNull URI serverUri, @NonNull String password, @NonNull String clientName) {
        return addNode(name, serverUri, password, clientName, null);
    }

    /**
     *
     * @param name
     *         A name to identify this node. May show up in metrics and other places.
     * @param serverUri
     *         uri of the node to be added
     * @param password
     *         password of the node to be added
     * @param resumeKey
     *        resume key of the node to be added
     * @throws IllegalStateException if no userId has been set.
     * @throws IllegalArgumentException if a node with that name already exists.
     * @see #setUserId(String)
     */
    @SuppressWarnings("WeakerAccess")
    public AteneaSocket addNode(@NonNull String name, @NonNull URI serverUri, @NonNull String password, @NonNull String clientName, @Nullable String resumeKey) {
        if (userId == null) {
            throw new IllegalStateException("We need a userId to connect to Atenea");
        }

        if (nodes.stream().anyMatch(sock -> sock.getName().equals(name))) {
            throw new IllegalArgumentException("A node with the name " + name + " already exists.");
        }

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", password);
        headers.put("Num-Shards", Integer.toString(numShards));
        headers.put("User-Id", userId);
        headers.put("Client-Name", clientName);
        if (resumeKey != null) {
            headers.put("Resume-Key", resumeKey);
        }

        AteneaSocket socket = new AteneaSocket(name, this, serverUri, new Draft_6455(), headers);
        socket.connect();
        nodes.add(socket);
        return socket;
    }

    @SuppressWarnings("unused")
    public void removeNode(int key) {
        AteneaSocket node = nodes.remove(key);
        node.close();
    }

    @SuppressWarnings("unused")
    @NonNull
    public AteneaLoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public T getLink(@NonNull String guildId) {
        return links.computeIfAbsent(guildId, __ -> buildNewLink(guildId));
    }

    @SuppressWarnings("WeakerAccess")
    @Nullable
    public T getExistingLink(@NonNull String guildId) {
        return links.get(guildId);
    }

    /**
     * Hook to build a new Link.
     * Since the Link class is abstract, you will have to return your own implementation of Link.
     *
     * @param guildId the associated guild's ID
     * @return the new link
     */
    protected abstract T buildNewLink(String guildId);

    @SuppressWarnings({"WeakerAccess", "unused"})
    public int getNumShards() {
        return numShards;
    }

    /**
     * The number of shards this bot is running on.
     * @throws IllegalStateException if any nodes are registered.
     */
    @SuppressWarnings("unused")
    public void setNumShards(int numShards) {
        if (!nodes.isEmpty()) {
            throw new IllegalStateException("Can't set numShards if we already have nodes registered!");
        }
        this.numShards = numShards;
    }

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public Collection<T> getLinks() {
        return links.values();
    }

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public List<AteneaSocket> getNodes() {
        return nodes;
    }

    /**
     * The user id of this bot.
     * @throws IllegalStateException if any nodes are registered.
     */
    @SuppressWarnings("unused")
    public void setUserId(@Nullable String userId) {
        if (!nodes.isEmpty()) {
            throw new IllegalStateException("Can't set userId if we already have nodes registered!");
        }
        this.userId = userId;
    }

    public void shutdown() {
        reconnectService.shutdown();
        nodes.forEach(ReusableWebSocket::close);
    }

    void removeDestroyedLink(Link link) {
        log.debug("Destroyed link for guild " + link.getGuildId());
        links.remove(link.getGuildId());
    }

    @SuppressWarnings("WeakerAccess")
    protected Map<String, T> getLinksMap() {
        return links;
    }

}
