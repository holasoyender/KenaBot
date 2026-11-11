/*
 * File:          PlayerPauseEvent.java
 * Path:          v3\src\main\java\atenea\driver\player\event\PlayerPauseEvent.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: ef89aa262ffceb0e951a432263bde727542df40090d2412ef1e516f391020b81
 *
 */


package atenea.driver.player.event;

import atenea.driver.player.IPlayer;

public class PlayerPauseEvent extends PlayerEvent {
    public PlayerPauseEvent(IPlayer player) {
        super(player);
    }
}
