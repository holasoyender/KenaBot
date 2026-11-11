/*
 * File:          ConfigHandler.kt
 * Path:          v3\src\main\kotlin\handlers\ConfigHandler.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-08 19:37:19 +01:00
 *
 * SHA-256: d2ae7b603a77d346f855fed168e4089af08a34b32cf840e6e22b49724503ca8b
 *
 */


package handlers

import database.schema.Guild
import handlers.config.*
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl

object ConfigHandler {

    fun handleButton(event: ButtonInteractionEvent, config: Guild, lang: LangUtils, args: List<String>) {

        when (args[2]) {
            "lang" -> Lang.goto(event, config, lang)
            "channels" -> Channels.goto(event, config, lang)
            "dj" -> DjRole.goto(event, config, lang)
            "commands" -> Commands.goto(event, config, lang)
            "voicechannel" -> VoiceChannels.goto(event, config, lang)
            "embed" -> Embeds.goto(event, config, lang)
            "premium" -> Premium.click(event, config, lang)
            "beta" -> Beta.click(event, config, lang)
            "silent" -> Silent.click(event, config, lang)

            "channels_selectall" -> Channels.selectAll(event, config, lang)
            "channels_desselectall" -> Channels.desSelectAll(event, config, lang)

            "voicechannels_selectall" -> VoiceChannels.selectAll(event, config, lang)
            "voicechannels_desselectall" -> VoiceChannels.desSelectAll(event, config, lang)

            "commands_selectall" -> Commands.selectAll(event, config, lang)
            "commands_desselectall" -> Commands.desSelectAll(event, config, lang)

            "more" -> {

                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setTitle(lang.get("commands.config.title"))
                    .setDescription(lang.get("commands.config.description").replace("%s%", event.jda.selfUser.id))


                val betaButton = if (config.beta) Button.success(
                    "cmd:config:beta:${event.user.id}",
                    lang.get("commands.config.buttons.beta_on")
                ) else
                    Button.danger("cmd:config:beta:${event.user.id}", lang.get("commands.config.buttons.beta_off"))

                val silentButton = if (config.silent) Button.success(
                    "cmd:config:silent:${event.user.id}",
                    lang.get("commands.config.buttons.silent_on")
                ) else
                    Button.danger("cmd:config:silent:${event.user.id}", lang.get("commands.config.buttons.silent_off"))

                event.editMessageEmbeds(embed.build()).setComponents(
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

            "back" -> {

                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setTitle(lang.get("commands.config.title"))
                    .setDescription(lang.get("commands.config.description").replace("%s%", event.jda.selfUser.id))

                val premiumButton =
                    if (config.premiumEnabled)
                        Button.primary(
                            "cmd:config:premium:${event.user.id}",
                            lang.get("commands.config.buttons.premium_on")
                        )
                    else
                        Button.danger(
                            "cmd:config:premium:${event.user.id}",
                            lang.get("commands.config.buttons.premium_off")
                        )


                event.editMessageEmbeds(embed.build()).setComponents(
                    ActionRow.of(
                        Button.secondary("cmd:config:lang:${event.user.id}", lang.get("commands.config.buttons.lang"))
                            .withEmoji(Emoji.fromUnicode(lang.get("flag"))),
                        Button.secondary(
                            "cmd:config:channels:${event.user.id}",
                            lang.get("commands.config.buttons.channels")
                        )
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("channels", 940254447530016859, false))),
                        Button.secondary("cmd:config:dj:${event.user.id}", lang.get("commands.config.buttons.dj"))
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("mention", 940268589615812608, false))),
                        Button.secondary("cmd:config:more:${event.user.id}", lang.get("commands.config.buttons.more"))
                    ), ActionRow.of(
                        Button.secondary(
                            "cmd:config:commands:${event.user.id}",
                            lang.get("commands.config.buttons.commands")
                        )
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("slash", 941024012270710874, false))),
                        premiumButton,
                        Button.danger("cmd:config:sync:${event.user.id}", Emoji.fromCustom(CustomEmojiImpl("loop", 952242523521294456, false))).withDisabled(event.jda.selfUser.name == "KenaBot")
                    )
                ).queue()
            }

            "extra" -> Extra.goto(event, config, lang)
            "explicit" -> Extra.explicit(event, config, lang)
            "sync" -> {
                event.reply(lang.get("commands.config.sync.confirm"))
                    .addActionRow(
                        Button.danger("cmd:config:sync-confirm:${event.user.id}", lang.get("commands.config.sync.confirm_button"))
                    )
                    .setEphemeral(true).queue()
            }
            "sync-confirm" -> Extra.syncConfig(event, lang)
            "durationLimit" -> Extra.durationLimit(event, config, lang)
            "perUserSongs" -> {
                event.reply("Coming soon!").setEphemeral(true).queue()
            }

            else -> event.reply(lang.get("events.interactionCreate.unknown")).setEphemeral(true).queue()
        }
    }

    fun handleMenu(event: StringSelectInteractionEvent, config: Guild, lang: LangUtils, args: List<String>) {

        when (args[2]) {
            "setlang" -> Lang.set(event, config, lang)
            "setdchannel" -> Channels.set(event, config, lang)
            "setdj" -> DjRole.set(event, config, lang)
            "disablecommand" -> Commands.set(event, config, lang)
            "setvoicechannel" -> VoiceChannels.set(event, config, lang)
            "setcolor" -> Embeds.set(event, config, lang)

            else -> event.reply(lang.get("events.interactionCreate.unknown")).setEphemeral(true).queue()
        }
    }

    fun handleModal(event: ModalInteractionEvent, config: Guild, lang: LangUtils, args: List<String>) {

        when (args[1]) {
            "durationLimit" -> Extra.durationLimit(event, config, lang)
            else -> event.reply(lang.get("events.interactionCreate.unknown")).setEphemeral(true).queue()
        }
    }
}