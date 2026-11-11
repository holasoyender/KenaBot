/*
 * File:          ReconnectTask.java
 * Path:          v3\src\main\java\atenea\driver\io\ReconnectTask.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-16 19:16:14 +02:00
 *
 * SHA-256: 70ba44afafb2c6b40ce8ccfa34034db176112694a7619e34040d9921d11d8fd1
 *
 */


package atenea.driver.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReconnectTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ReconnectTask.class);
    private final Atenea atenea;

    ReconnectTask(Atenea atenea) {
        this.atenea = atenea;
    }

    @Override
    public void run() {
        try {
            @SuppressWarnings("unchecked") List<AteneaSocket> nodes = atenea.getNodes();
            nodes.forEach(ateneaSocket -> {
                if (ateneaSocket.isClosed()
                        && !ateneaSocket.isConnecting()
                        && System.currentTimeMillis() - ateneaSocket.lastReconnectAttempt > ateneaSocket.getReconnectInterval()) {
                    ateneaSocket.attemptReconnect();
                }
            });
        } catch (Exception e) {
            log.error("Caught exception in reconnect thread", e);
        }
    }
}
