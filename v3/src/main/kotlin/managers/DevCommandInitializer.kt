/*
 * File:          DevCommandInitializer.kt
 * Path:          v3\src\main\kotlin\managers\DevCommandInitializer.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-07 01:11:56 +02:00
 *
 * SHA-256: 3711b26fb005ab0822419d5e0a3ccb4ad05363d7f0ab61c9037402ee707767cb
 *
 */


package managers

import commands.dev.*

class DevCommandInitializer(manager: DevCommandManager) {

    init {
        manager.registerCommand(Ping())
        manager.registerCommand(Eval())
        manager.registerCommand(Memory())
        manager.registerCommand(Shutdown())
        manager.registerCommand(Top())
        manager.registerCommand(Status())
        manager.registerCommand(Stats())
        manager.registerCommand(Block())
        manager.registerCommand(Unblock())
        manager.registerCommand(Premium())
        manager.registerCommand(Partner())
        manager.registerCommand(Restart())
        manager.registerCommand(Whitelist())
        manager.registerCommand(Playing())
        manager.registerCommand(Keys())
        manager.registerCommand(Nodes())
    }
}