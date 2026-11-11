/*
 * File:          Beta.kt
 * Path:          v3\src\main\kotlin\handlers\config\Beta.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 188ceb20ec8e55d23714641262b74c1de46f339096c38b05702216d0098eb63b
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

object Beta {
    fun click(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        if(!config.premiumEnabled) {
            event.reply(lang.get("manager.no_premium")).setEphemeral(true).queue()
            return
        }

        config.beta = !config.beta
        config.save()

        val betaButton = if ((config.found && !config.beta) || config.beta) Button.danger(
            "cmd:config:beta:${event.user.id}",
            lang.get("commands.config.buttons.beta_off")
        ) else
            Button.success("cmd:config:beta:${event.user.id}", lang.get("commands.config.buttons.beta_on"))

        val silentButton = if (config.silent) Button.success(
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