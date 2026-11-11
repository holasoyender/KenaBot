/*
 * File:          Silent.kt
 * Path:          v3\src\main\kotlin\handlers\config\Silent.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 012c0de54d5ef84ead87965b29df074da94e211432f4732f8490a962f590baba
 *
 */


package handlers.config

import database.schema.Guild
import langs.LangUtils
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl

object Silent {

    fun click(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        config.silent = !config.silent
        config.save()

        val betaButton = if (config.beta) Button.success(
            "cmd:config:beta:${event.user.id}",
            lang.get("commands.config.buttons.beta_on")
        ) else
            Button.danger("cmd:config:beta:${event.user.id}", lang.get("commands.config.buttons.beta_off"))

        val silentButton = if ((config.found && config.silent) || config.silent) Button.success(
            "cmd:config:silent:${event.user.id}",
            lang.get("commands.config.buttons.silent_on")
        ) else
            Button.danger("cmd:config:silent:${event.user.id}", lang.get("commands.config.buttons.silent_off"))

        event.editComponents(
            ActionRow.of(
                Button.secondary(
                    "cmd:config:voicechannel:${event.user.id}",
                    lang.get("commands.config.buttons.voice_channels")
                ).withEmoji(
                    Emoji.fromCustom(CustomEmojiImpl("vocielock", 940653745556037643, false))
                ),
                betaButton,
                Button.secondary("cmd:config:embed:${event.user.id}", lang.get("commands.config.buttons.embed"))
            ),
            ActionRow.of(
                Button.secondary("cmd:config:back:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
                silentButton,
                Button.secondary("cmd:config:extra:${event.user.id}", lang.get("commands.config.buttons.extra"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("add", 952249506680959026, false))
                    ),
            )
        ).queue()
    }
}