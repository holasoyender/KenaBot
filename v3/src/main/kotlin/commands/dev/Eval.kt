/*
 * File:          Eval.kt
 * Path:          v3\src\main\kotlin\commands\dev\Eval.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 16:20:00 +02:00
 *
 * SHA-256: 27f0baaf35a69c7bff918d0fa637f0026b6e2830b3fa591101bfbfa9739f47af
 *
 */


package commands.dev

import database.schema.Guild
import interfaces.DevCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import player.Player
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class Eval: DevCommand {
    override fun run(event: MessageReceivedEvent, args: List<String>) {

        val toEval: String = args.subList(1, args.size).joinToString(" ")

        if (toEval.isEmpty()) {
            event.message.reply("Debes de especificar un código a evaluar").queue()
            return
        }

        val script = ScriptEngineManager().getEngineByName("nashorn")

        script.eval("var imports = new JavaImporter(java.io, java.lang, java.util, java.net);")
        script.put("client", event.jda)
        script.put("shard", event.jda.shardManager)
        script.put("channel", event.channel)
        script.put("message", event.message)
        if(event.isFromGuild) {
            script.put("guild", event.guild)
            script.put("member", event.member)
            script.put("config", Guild.get(event.guild.id))
        }
        script.put("player", Player)

        try {
            val res = script.eval("with (imports) { $toEval }")
            val result = res?.toString() ?: "void"
            event.message.reply("```java\n${result.replace(event.jda.token.toRegex(), "T0K3N")}```").queue()
        } catch (ex: ScriptException) {
            event.message.reply("```java\n${ex.message!!.replace(event.jda.token.toRegex(), "T0K3N")}```").queue()
        }
    }

    override val name: String
        get() = "eval"
    override val description: String
        get() = "Evaluar código"
}