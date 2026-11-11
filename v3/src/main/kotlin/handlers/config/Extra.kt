/*
 * File:          Extra.kt
 * Path:          v3\src\main\kotlin\handlers\config\Extra.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-05-29 13:53:04 +02:00
 *
 * SHA-256: 68aa00cf0e29a0557a6c29a55ebc57e668f016aa67c4545ec2673f05dcd053ce
 *
 */


package handlers.config

import database.Database
import database.schema.Guild
import langs.LangUtils
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl
import org.bson.Document
import utils.Constants
import kotlin.time.Duration.Companion.milliseconds

object Extra {

    fun goto(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        val explicitButton = if(config.explicitFilter)
            Button.success("cmd:config:explicit:${event.user.id}", lang.get("commands.config.buttons.explicit_on"))
                .withEmoji(
                    Emoji.fromCustom(CustomEmojiImpl("explicit", 1073266738759729184, false))
                )
        else
            Button.danger("cmd:config:explicit:${event.user.id}", lang.get("commands.config.buttons.explicit_off"))
                .withEmoji(
                    Emoji.fromCustom(CustomEmojiImpl("explicit", 1073266738759729184, false))
                )
        event.editComponents(
            ActionRow.of(
                explicitButton,
                Button.secondary("cmd:config:durationLimit:${event.user.id}", lang.get("commands.config.buttons.duration_limit")),
                Button.secondary("cmd:config:perUserSongs:${event.user.id}", lang.get("commands.config.buttons.per_user_songs")).withEmoji(
                    Emoji.fromCustom(CustomEmojiImpl("stage", 991673058668576828, false))
                ),
            ),
            ActionRow.of(
                Button.secondary("cmd:config:more:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
            )
        ).queue()

    }

    fun explicit(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {
        config.explicitFilter = !config.explicitFilter
        config.save()
        goto(event, config, lang)
    }

    fun durationLimit(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {
        if (!config.premiumEnabled) {
            event.reply(lang.get("manager.no_premium")).setEphemeral(true).queue()
            return
        }

        val duration = config.maxSongDuration.milliseconds

        val body: TextInput = TextInput.create("body", lang.get("commands.config.duration_limit.title"), TextInputStyle.SHORT)
            .setPlaceholder(lang.get("commands.config.duration_limit.placeholder").replace("%s%", "$duration"))
            .setMinLength(1)
            .setMaxLength(6)
            .build()

        val modal: Modal = Modal.create("config:durationLimit", lang.get("commands.config.duration_limit.big_title"))
            .addComponents(ActionRow.of(body))
            .build()

        event.replyModal(modal).queue()

    }

    fun durationLimit(event: ModalInteractionEvent, config: Guild, lang: LangUtils) {

        val body = (event.getValue("body") ?: return event.reply(lang.get("commands.config.duration_limit.no_duration")).queue()).asString

        if(body.contains("h")) return event.reply(lang.get("commands.config.duration_limit.max_duration")).setEphemeral(true).queue()

        val split = body.split("m")
        val minutes = split[0].toIntOrNull() ?: 0
        val seconds = if (split.size > 1) split[1].replace("s", "").toIntOrNull() ?: 0 else if (split[0].contains("s")) split[0].replace("s", "").toIntOrNull() ?: 0 else 0

        val duration = (minutes * 60 + seconds) * 1000L

        if(duration > 600000) return event.reply(lang.get("commands.config.duration_limit.max_duration")).setEphemeral(true).queue()
        config.maxSongDuration = duration
        config.save()

        event.reply("${Constants.Emojis.success}  ${lang.get("commands.config.duration_limit.success").replace("%s%", if(duration == 0L) "0s" else body)}").setEphemeral(true).queue()
    }

    fun syncConfig(event: ButtonInteractionEvent, lang: LangUtils) {
        val db = Database.syncDatabase
        if (db == null) {
            event.editMessage("${Constants.Emojis.error}  ${lang.get("commands.config.sync.error")}")
                .setComponents()
                .queue()
            return
        }

        val syncConfig = db.getCollection("Guilds").find(Document("guildID", event.guild!!.id)).firstOrNull()
        if (syncConfig == null) {
            event.editMessage("${Constants.Emojis.error}  ${lang.get("commands.config.sync.error")}")
                .setComponents()
                .queue()
            return
        }

        val syncConfigFormatted = Guild.formatDocument(syncConfig)
        syncConfigFormatted.guildID = event.guild!!.id

        syncConfigFormatted.save()
        event.editMessage("${Constants.Emojis.success}  ${lang.get("commands.config.sync.success")}")
            .setComponents()
            .queue()
    }
}