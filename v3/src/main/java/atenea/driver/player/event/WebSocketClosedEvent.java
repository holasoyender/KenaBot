/*
 * File:          WebSocketClosedEvent.java
 * Path:          v3\src\main\java\atenea\driver\player\event\WebSocketClosedEvent.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 23:59:04 +02:00
 *
 * SHA-256: bccfa62001fbbf1936248b2f2f50ae307bb4cd99cc479a6af777d6aa610f80c0
 *
 */


package atenea.driver.player.event;

import atenea.driver.player.IPlayer;

public class WebSocketClosedEvent extends PlayerEvent{
    private final int code;
    private final String reason;
    private final boolean remote;

    public WebSocketClosedEvent(IPlayer player, int code, String reason, boolean remote){
        super(player);
        this.code = code;
        this.reason = reason;
        this.remote = remote;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public boolean isRemote() {
        return remote;
    }
}
