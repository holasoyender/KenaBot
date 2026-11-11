/*
 * File:          LastState.kt
 * Path:          v3\src\main\kotlin\functions\LastState.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 50cc3ee9a0faa77aca4064c959a57d07e24244451aedbe9626269b69541b9b46
 *
 */


package functions

import enums.States

object LastState {

    private var lastState: States = States.UNKNOWN
    private var readyAt: Long = System.currentTimeMillis()

    fun getLastState(): States {
        return lastState
    }
    fun setLastState(state: States) {
        lastState = state
    }

    fun getReadyAt(): Long {
        return readyAt
    }

    fun setReadyAt(time: Long) {
        readyAt = time
    }
}