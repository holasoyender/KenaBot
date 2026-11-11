/*
 * File:          PenaltyProvider.java
 * Path:          v3\src\main\java\atenea\driver\io\PenaltyProvider.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: f6179bbaf4705ca95b506fb7aee74f59810704d8744d3c316dd2a50f57e96666
 *
 */


package atenea.driver.io;

public interface PenaltyProvider {

    /**
     * This method allows for adding custom penalties to {@link AteneaSocket nodes}, making it possible to
     * change how the node selection system works on a per-guild per-node basis.
     * By using the provided {@link AteneaLoadBalancer.Penalties Penalties} class you can fetch default penalties like CPU or Players.
     *
     * @param penalties - Instance of {@link AteneaLoadBalancer.Penalties Penalties} class representing the node to check.
     * @return total penalty to add to this node.
     */
    int getPenalty(AteneaLoadBalancer.Penalties penalties);

}
