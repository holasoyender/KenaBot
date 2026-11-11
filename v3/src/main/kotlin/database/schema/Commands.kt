/*
 * File:          Commands.kt
 * Path:          v3\src\main\kotlin\database\schema\Commands.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: b385f6ba021d2dccbb2d0d66cb7e56ce013d2b7f16451d30f3e4c8ba9ed590a6
 *
 */


package database.schema

import database.Database
import org.bson.Document

class Commands(
    command: String,
    uses: Int
) {

    var command: String
    var uses: Int

    init {
        this.command = command
        this.uses = uses
    }

    fun increment(): Commands {
        this.uses++
         Database.statsDatabase!!.getCollection("Commands").findOneAndUpdate(
                Document("command", this.command),
                Document("\$inc", Document("uses", 1))
            )

        return this
    }


    companion object {

        fun get(command: String): Commands {

            val cmd = Database.statsDatabase!!.getCollection("Commands").find(Document("command", command)).firstOrNull()

            return if (cmd != null)
                Commands(cmd.getString("command"), cmd.getInteger("uses"))
            else {
                Database.statsDatabase!!.getCollection("Commands").insertOne(Document("command", command).append("uses", 1))
                Commands(command, 1)
            }

        }

        fun getAll(): List<Commands> {

            val commands = Database.statsDatabase!!.getCollection("Commands").find()

            return commands.toList().map {
                Commands(it.getString("command"), it.getInteger("uses"))
            }

        }

    }

}