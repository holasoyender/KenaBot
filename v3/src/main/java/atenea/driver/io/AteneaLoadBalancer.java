/*
 * File:          AteneaLoadBalancer.java
 * Path:          v3\src\main\java\atenea\driver\io\AteneaLoadBalancer.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-16 19:16:14 +02:00
 *
 * SHA-256: 236b323a00674c9538f996013edfe81d40b03471611c87feeba528803b2ab13f
 *
 */


package atenea.driver.io;

import com.mongodb.lang.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class AteneaLoadBalancer {

    private final Atenea atenea;
    //private Map<String, Optional<AteneaSocket>> socketMap = new ConcurrentHashMap<>();
    private final List<PenaltyProvider> penaltyProviders = new ArrayList<>();

    AteneaLoadBalancer(Atenea atenea) {
        this.atenea = atenea;
    }

    @NonNull
    public AteneaSocket determineBestSocket(long guild) {
        AteneaSocket leastPenalty = null;
        int record = Integer.MAX_VALUE;

        @SuppressWarnings("unchecked")
        List<AteneaSocket> nodes = atenea.getNodes();
        for (AteneaSocket socket : nodes) {
            int total = getPenalties(socket, guild, penaltyProviders).getTotal();
            if (total < record) {
                leastPenalty = socket;
                record = total;
            }
        }

        if (leastPenalty == null || !leastPenalty.isAvailable())
            throw new IllegalStateException("No available nodes!");

        return leastPenalty;
    }

    @SuppressWarnings("unused")
    public void addPenalty(PenaltyProvider penalty) {
        this.penaltyProviders.add(penalty);
    }

    @SuppressWarnings("unused")
    public void removePenalty(PenaltyProvider penalty) {
        this.penaltyProviders.remove(penalty);
    }

    void onNodeDisconnect(AteneaSocket disconnected) {
        @SuppressWarnings("unchecked") Collection<Link> links = atenea.getLinks();
        links.forEach(link -> {
            if (disconnected.equals(link.getNode(false)))
                link.changeNode(atenea.loadBalancer.determineBestSocket(link.getGuildIdLong()));
        });
    }

    void onNodeConnect(AteneaSocket connected) {
        @SuppressWarnings("unchecked")
        List<AteneaSocket> sockets = atenea.getNodes();
        long otherAvailableNodes = sockets.stream()
                .filter(node -> node != connected)
                .filter(AteneaSocket::isAvailable)
                .count();
        if (otherAvailableNodes > 0) { //only update links if this is the only connected node
            return;
        }
        @SuppressWarnings("unchecked")
        Collection<Link> links = atenea.getLinks();
        links.forEach(link -> {
            if (link.getNode(false) == null)
                link.changeNode(connected);
        });
    }

    public Penalties getPenalties(AteneaSocket socket, long guild, List<PenaltyProvider> penaltyProviders) {
        return new Penalties(socket, guild, penaltyProviders, atenea);
    }

    @SuppressWarnings("unused")
    public static Penalties getPenalties(AteneaSocket socket) {
        return new Penalties(socket, 0L, Collections.emptyList(), null);
    }

    @SuppressWarnings("unused")
    public static class Penalties {

        private final AteneaSocket socket;
        private final long guild;
        private int playerPenalty = 0;
        private int cpuPenalty = 0;
        private int deficitFramePenalty = 0;
        private int nullFramePenalty = 0;
        private int customPenalties = 0;
        private final Atenea atenea;

        private Penalties(AteneaSocket socket, long guild, List<PenaltyProvider> penaltyProviders, Atenea atenea) {
            this.atenea = atenea;
            this.socket = socket;
            this.guild = guild;
            RemoteStats stats = socket.getStats();
            if (stats == null) return; // Will return as max penalty anyways
            // This will serve as a rule of thumb. 1 playing player = 1 penalty point
            if (atenea != null) {
                playerPenalty = countPlayingPlayers();
            } else {
                playerPenalty = stats.getPlayingPlayers();
            }

            // https://fred.moe/293.png
            cpuPenalty = (int) Math.pow(1.05d, 100 * stats.getSystemLoad()) * 10 - 10;

            // -1 Means we don't have any frame stats. This is normal for very young nodes
            if (stats.getAvgFramesDeficitPerMinute() != -1) {
                // https://fred.moe/rjD.png
                deficitFramePenalty = (int) (Math.pow(1.03d, 500f * ((float) stats.getAvgFramesDeficitPerMinute() / 3000f)) * 600 - 600);
                nullFramePenalty = (int) (Math.pow(1.03d, 500f * ((float) stats.getAvgFramesNulledPerMinute() / 3000f)) * 300 - 300);
                nullFramePenalty *= 2;
                // Deficit frames are better than null frames, as deficit frames can be caused by the garbage collector
            }
            penaltyProviders.forEach(pp -> customPenalties += pp.getPenalty(this));
        }

        private int countPlayingPlayers() {

            @SuppressWarnings("unchecked") Collection<Link> links = atenea.getLinks();
            long players = links
                    .stream().filter(link ->
                            socket.equals(link.getNode(false)) &&
                                    link.getPlayer().getPlayingTrack() != null &&
                                    !link.getPlayer().isPaused())
                    .count();
            return (int) players;
        }

        public AteneaSocket getSocket() {
            return socket;
        }

        public long getGuild() {
            return guild;
        }

        public int getPlayerPenalty() {
            return playerPenalty;
        }

        public int getCpuPenalty() {
            return cpuPenalty;
        }

        public int getDeficitFramePenalty() {
            return deficitFramePenalty;
        }

        public int getNullFramePenalty() {
            return nullFramePenalty;
        }

        public int getCustomPenalties() {
            return this.customPenalties;
        }

        public int getTotal() {
            if (!socket.isAvailable() || socket.getStats() == null) return (Integer.MAX_VALUE - 1);
            return playerPenalty + cpuPenalty + deficitFramePenalty + nullFramePenalty + customPenalties;
        }

        @Override
        public String toString() {
            if (!socket.isAvailable()) return "Penalties{" +
                    "unavailable=" + (Integer.MAX_VALUE - 1) +
                    '}';

            return "Penalties{" +
                    "total=" + getTotal() +
                    ", playerPenalty=" + playerPenalty +
                    ", cpuPenalty=" + cpuPenalty +
                    ", deficitFramePenalty=" + deficitFramePenalty +
                    ", nullFramePenalty=" + nullFramePenalty +
                    ", custom=" + customPenalties +
                    '}';
        }
    }

}
