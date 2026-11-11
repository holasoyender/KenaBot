/*
 * File:          PlayerResumeEvent.java
 * Path:          v3\src\main\java\atenea\driver\player\event\PlayerResumeEvent.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: e1611954b97a17991ea9f3fac3d8170a8eb9d8f4cce807ed380af337923d70e1
 *
 */


package atenea.driver.player.event;

import atenea.driver.player.IPlayer;

public class PlayerResumeEvent extends PlayerEvent {
    public PlayerResumeEvent(IPlayer player) {
        super(player);
    }
}
