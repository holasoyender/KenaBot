/*
 * File:          ExpiredSessions.kt
 * Path:          v3\src\main\kotlin\services\ExpiredSessions.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 1fa7770fca0a4fa7621ee3d197c8cdf87b0f569c9791152e4849d4c697f29b77
 *
 */


package services

import database.schema.Session
import kotlin.concurrent.thread

object ExpiredSessions {

    fun startLoop() {

        thread( isDaemon = true, name = "ExpiredSessionsService") {
            while (true) {
                val allSessions = Session.getAll()
                allSessions.forEach { session ->

                    val currentTime = System.currentTimeMillis()
                    if(session.createdAt + 86400000 <= currentTime) {
                        session.delete()
                        return@forEach
                    }

                    if (session.createdAt + 900000 <= currentTime) {
                        if(session.listeners.isEmpty())
                            session.delete()
                    }
                }
                Thread.sleep(120000)
            }
        }

    }

}