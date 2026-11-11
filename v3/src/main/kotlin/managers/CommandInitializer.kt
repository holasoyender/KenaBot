/*
 * File:          CommandInitializer.kt
 * Path:          v3\src\main\kotlin\managers\CommandInitializer.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 953108212a9c91461a3b6c2d661bd377064202a31ebe93b31a3be4994d5fd929
 *
 */


package managers

import commands.*

class CommandInitializer(manager: CommandManager) {

    init {
        manager.registerCommand(Ping())
        manager.registerCommand(Support())
        manager.registerCommand(Help())
        manager.registerCommand(Commands())
        manager.registerCommand(Play())
        manager.registerCommand(Leave())
        manager.registerCommand(Loop())
        manager.registerCommand(Pause())
        manager.registerCommand(Resume())
        manager.registerCommand(Skip())
        manager.registerCommand(Stop())
        manager.registerCommand(Volume())
        manager.registerCommand(Clearqueue())
        manager.registerCommand(Nowplaying())
        manager.registerCommand(Shuffle())
        manager.registerCommand(Seek())
        manager.registerCommand(Invite())
        manager.registerCommand(Dashboard())
        manager.registerCommand(Premium())
        manager.registerCommand(Back())
        manager.registerCommand(Filter())
        manager.registerCommand(Queue())
        manager.registerCommand(Remove())
        manager.registerCommand(Skipto())
        manager.registerCommand(Status())
        manager.registerCommand(Session())
        manager.registerCommand(Lyrics())
        manager.registerCommand(Together())
        manager.registerCommand(`247`())
        manager.registerCommand(Config())
        manager.registerCommand(Block())
        manager.registerCommand(Unblock())
        manager.registerCommand(Artist())
        manager.registerCommand(Autoplay())
    }
}
