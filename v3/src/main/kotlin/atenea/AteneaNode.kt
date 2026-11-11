/*
 * File:          AteneaNode.kt
 * Path:          v3\src\main\kotlin\atenea\AteneaNode.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: ed2c376006d908216114315f489e47449946f420f639d58234ce05e8ad96a16e
 *
 */


package atenea

import enums.NodeState

class AteneaNode(
    host: String,
    password: String,
    region: String,
    name: String,
    state: NodeState
) {

    val host: String
    val password: String
    val region: String
    val name: String
    var state: NodeState

    init {
        this.host = host
        this.password = password
        this.region = region
        this.name = name
        this.state = state
    }
}