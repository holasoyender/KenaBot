/*
 * File:          PlayerEvent.java
 * Path:          v3\src\main\java\atenea\driver\player\event\PlayerEvent.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: 86e380ba894684ce4cf7d6e49407fd92a05502b6ac997f0265fc9daa3c228eaf
 *
 */


package atenea.driver.player.event;

import atenea.driver.player.IPlayer;

public abstract class PlayerEvent {

    private final IPlayer player;

    public PlayerEvent(IPlayer player) {
        this.player = player;
    }

    public IPlayer getPlayer() {
        return player;
    }
}
