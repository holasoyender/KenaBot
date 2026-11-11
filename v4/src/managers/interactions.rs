/*
 * File:          interactions.rs
 * Path:          v4\src\managers\interactions.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-03-10 19:48:01 +01:00
 *
 * SHA-256: d0cf7359c2230a97dcf4e1729bc0738ce1b121b077b1259bf82bc79da57643ad
 *
 */


use skua::all::{ApplicationId, Builder, ButtonStyle, ChannelId, CommandData, CommandInteraction, CreateActionRow, CreateAllowedMentions, CreateAttachment, CreateButton, CreateEmbed, CreateInteractionResponse, CreateInteractionResponseFollowup, CreateInteractionResponseMessage, GuildId, InteractionId, InteractionResponseFlags, Member, PartialChannel, Permissions, User};
use serde::{Deserialize, Deserializer, Serialize};
use skua::http::CacheHttp;
use shared::emojis::Emoji;

#[derive(Clone, Debug, Deserialize, Serialize)]
#[serde(remote = "Self")]
#[non_exhaustive]
pub struct KenaCommandInteraction {
    /// Id of the interaction.
    pub id: InteractionId,
    /// Id of the application this interaction is for.
    pub application_id: ApplicationId,
    /// The data of the interaction which was triggered.
    pub data: CommandData,
    /// The guild Id this interaction was sent from, if there is one.
    #[serde(skip_serializing_if = "Option::is_none")]
    pub guild_id: Option<GuildId>,
    /// Channel that the interaction was sent from.
    pub channel: Option<PartialChannel>,
    /// The channel Id this interaction was sent from.
    pub channel_id: ChannelId,
    /// The `member` data for the invoking user.
    ///
    /// **Note**: It is only present if the interaction is triggered in a guild.
    #[serde(skip_serializing_if = "Option::is_none")]
    pub member: Option<Box<Member>>,
    /// The `user` object for the invoking user.
    #[serde(default)]
    pub user: User,
    /// A continuation token for responding to the interaction.
    pub token: String,
    /// Always `1`.
    pub version: u8,
    /// Permissions the app or bot has within the channel the interaction was sent from.
    pub app_permissions: Option<Permissions>,
    /// The selected language of the invoking user.
    pub locale: String,
    /// The guild's preferred locale.
    pub guild_locale: Option<String>,

    response: ResponseMessage,
}

#[derive(Clone, Debug, Serialize)]
pub struct ResponseMessage {
    #[serde(skip_serializing_if = "Option::is_none")]
    tts: Option<bool>,
    #[serde(skip_serializing_if = "Option::is_none")]
    content: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    embeds: Option<Vec<CreateEmbed>>,
    #[serde(skip_serializing_if = "Option::is_none")]
    allowed_mentions: Option<CreateAllowedMentions>,
    #[serde(skip_serializing_if = "Option::is_none")]
    flags: Option<InteractionResponseFlags>,
    #[serde(skip_serializing_if = "Option::is_none")]
    components: Option<Vec<CreateActionRow>>,
    //attachments: EditAttachments,
    attachments: Vec<CreateAttachment>,
    id: InteractionId,
    token: String,
    deferred: bool
}

impl<'de> Deserialize<'de> for ResponseMessage {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
        where
            D: Deserializer<'de>,
    {
        #[derive(Deserialize)]
        #[serde(field_identifier, rename_all = "snake_case")]
        enum Field {
            Tts,
            Content,
            Embeds,
            AllowedMentions,
            Flags,
            Components,
            Attachments,
            Deferred,
        }

        struct ResponseMessageVisitor;

        impl<'de> serde::de::Visitor<'de> for ResponseMessageVisitor {
            type Value = ResponseMessage;

            fn expecting(&self, formatter: &mut std::fmt::Formatter) -> std::fmt::Result {
                formatter.write_str("struct ResponseMessage")
            }

            fn visit_map<V>(self, mut map: V) -> Result<Self::Value, V::Error>
                where
                    V: serde::de::MapAccess<'de>,
            {
                let mut tts = None;
                let mut content = None;
                let mut embeds = None;
                let mut allowed_mentions = None;
                let mut flags = None;
                let mut components = None;
                let mut attachments = None;
                let mut deferred = false;

                while let Some(key) = map.next_key()? {
                    match key {
                        Field::Tts => tts = map.next_value()?,
                        Field::Content => content = map.next_value()?,
                        Field::Embeds => embeds = None,
                        Field::AllowedMentions => allowed_mentions = None,
                        Field::Flags => flags = map.next_value()?,
                        Field::Components => components = None,
                        Field::Attachments => attachments = None,
                        Field::Deferred => deferred = map.next_value()?,
                    }
                }

                Ok(ResponseMessage {
                    tts,
                    content,
                    embeds,
                    allowed_mentions,
                    flags,
                    components,
                    attachments: attachments.unwrap_or_default(),
                    id: InteractionId::default(),
                    token: "".to_string(),
                    deferred
                })
            }
        }

        const FIELDS: &[&str] = &[
            "tts",
            "content",
            "embeds",
            "allowed_mentions",
            "flags",
            "components",
            "attachments",
            "deferred"
        ];

        deserializer.deserialize_struct("ResponseMessage", FIELDS, ResponseMessageVisitor)
    }
}

impl KenaCommandInteraction {
    pub fn from_command_interaction(command_interaction: CommandInteraction) -> Self {
        KenaCommandInteraction {
            id: command_interaction.id,
            application_id: command_interaction.application_id,
            data: command_interaction.data,
            guild_id: command_interaction.guild_id,
            channel: command_interaction.channel,
            channel_id: command_interaction.channel_id,
            member: command_interaction.member,
            user: command_interaction.user,
            token: command_interaction.token.clone(),
            version: command_interaction.version,
            app_permissions: command_interaction.app_permissions,
            locale: command_interaction.locale,
            guild_locale: command_interaction.guild_locale,

            response: ResponseMessage {
                tts: None,
                content: None,
                embeds: None,
                allowed_mentions: None,
                flags: None,
                components: None,
                //attachments: EditAttachments::default(),
                attachments: Vec::new(),
                id: command_interaction.id,
                token: command_interaction.token,
                deferred: false
            },
        }
    }

    /*pub async fn reply(
        &self,
        cache_http: impl CacheHttp,
        message: &str,
    ) -> serenity::Result<()> {
        let builder = CreateInteractionResponse::Message(CreateInteractionResponseMessage::default().content(message));
        builder.execute(cache_http, (self.id, &self.token)).await
    }*/

    pub fn reply(
        &mut self,
    ) -> ResponseMessage {
        self.response.clone()
    }

    pub async fn defer(
        &mut self,
        cache_http: impl CacheHttp
    ) -> skua::Result<()> {
        let builder = CreateInteractionResponse::Defer(CreateInteractionResponseMessage::default());
        self.response.deferred = true;

        builder.execute(cache_http, (self.id, &self.token)).await
    }


    pub async fn defer_ephemeral(
        &self,
        cache_http: impl CacheHttp
    ) -> skua::Result<()> {
        let builder = CreateInteractionResponse::Defer(
            CreateInteractionResponseMessage::new().ephemeral(true),
        );
        builder.execute(cache_http, (self.id, &self.token)).await
    }
}

impl From<CommandInteraction> for KenaCommandInteraction {
    fn from(command_interaction: CommandInteraction) -> Self {
        KenaCommandInteraction::from_command_interaction(command_interaction)
    }
}

impl ResponseMessage {

    pub fn emoji(mut self, emoji: Emoji) -> Self {
        self.content = Some(emoji.format());
        self
    }

    pub fn content(mut self, content: impl Into<String>) -> Self {
        self.content = Some(self.content.unwrap_or_default() + &*content.into());
        self
    }

    pub fn tts(mut self, tts: bool) -> Self {
        self.tts = Some(tts);
        self
    }

    pub fn embed(mut self, embed: CreateEmbed) -> Self {
        self.embeds.get_or_insert_with(Vec::new).push(embed.into());
        self
    }

    pub fn embeds(mut self, embeds: Vec<CreateEmbed>) -> Self {
        self.embeds.get_or_insert_with(Vec::new).extend(embeds);
        self
    }

    pub fn allowed_mentions(mut self, allowed_mentions: CreateAllowedMentions) -> Self {
        self.allowed_mentions = Some(allowed_mentions);
        self
    }

    pub fn emphemeral(mut self) -> Self {
        self.flags = Some(InteractionResponseFlags::EPHEMERAL);
        self
    }

    pub fn suppress_embeds(mut self) -> Self {
        self.flags = Some(InteractionResponseFlags::SUPPRESS_EMBEDS);
        self
    }

    pub fn components(mut self, components: Vec<CreateActionRow>) -> Self {
        self.components = Some(components);
        self
    }

    pub fn component(mut self, component: CreateActionRow) -> Self {
        self.components.get_or_insert_with(Vec::new).push(component);
        self
    }

    pub fn link_button(mut self, _label: impl Into<String>, url: impl Into<String>, emoji: Option<Emoji>, disabled: bool) -> Self {
        let empty_vec: Vec<CreateActionRow> = Vec::new();
        let current_action_rows = match &self.components {
            Some(rows) => rows,
            None => {
                &empty_vec
            }
        };

        let label = _label.into();

        if current_action_rows.is_empty() {
            let mut button = CreateButton::new_link(url).disabled(disabled);
            if let Some(emoji) = emoji {
                button = button.emoji(emoji.reaction());
            }

            if label.len() > 0 {
                button = button.label(label);
            }


            self.components = Some(vec![
                CreateActionRow::Buttons(vec![button])
            ]);
        } else {
            let mut rows = current_action_rows.clone();
            let last_action_row = rows.last_mut();

            if let Some(CreateActionRow::Buttons(buttons)) = last_action_row {

                let mut button = CreateButton::new_link(url).disabled(disabled);
                if let Some(emoji) = emoji {
                    button = button.emoji(emoji.reaction());
                }

                if label.len() > 0 {
                    button = button.label(label);
                }

                if buttons.len() >= 5 {
                    self.components.get_or_insert_with(Vec::new).push(CreateActionRow::Buttons(vec![button]));
                } else {
                    buttons.push(button);
                    self.components.get_or_insert_with(Vec::new).pop();
                    self.components.get_or_insert_with(Vec::new).push(CreateActionRow::Buttons(buttons.clone()));
                }
            } else {
                if current_action_rows.len() >= 5 {
                    error!("Too many action rows");
                } else {
                    let mut button = CreateButton::new_link(url).disabled(disabled);
                    if let Some(emoji) = emoji {
                        button = button.emoji(emoji.reaction());
                    }

                    if label.len() > 0 {
                        button = button.label(label);
                    }

                    self.components = Some(vec![
                        CreateActionRow::Buttons(vec![button])
                    ]);
                }
            }
        }

        return self;
    }

    pub fn button(mut self, _label: impl Into<String>, _id: impl Into<String>, emoji: Option<Emoji>, disabled: bool, button_style: ButtonStyle) -> Self {
        let empty_vec: Vec<CreateActionRow> = Vec::new();
        let current_action_rows = match &self.components {
            Some(rows) => rows,
            None => {
                &empty_vec
            }
        };

        let id = _id.into();
        let label = _label.into();

        if current_action_rows.is_empty() {
            let mut button = CreateButton::new(id)
                .style(button_style)
                .disabled(disabled);
            if let Some(emoji) = emoji {
                button = button.emoji(emoji.reaction());
            }

            if label.len() > 0 {
                button = button.label(label);
            }

            self.components = Some(vec![
                CreateActionRow::Buttons(vec![button])
            ]);
        } else {
            let mut rows = current_action_rows.clone();
            let last_action_row = rows.last_mut();

            if let Some(CreateActionRow::Buttons(buttons)) = last_action_row {

                let mut button = CreateButton::new(id)
                    .style(button_style)
                    .disabled(disabled);
                if let Some(emoji) = emoji {
                    button = button.emoji(emoji.reaction());
                }

                if label.len() > 0 {
                    button = button.label(label);
                }

                if buttons.len() >= 5 {
                    self.components.get_or_insert_with(Vec::new).push(CreateActionRow::Buttons(vec![button]));
                } else {
                    buttons.push(button);
                    self.components.get_or_insert_with(Vec::new).pop();
                    self.components.get_or_insert_with(Vec::new).push(CreateActionRow::Buttons(buttons.clone()));
                }
            } else {
                if current_action_rows.len() >= 5 {
                    error!("Too many action rows");
                } else {
                    let mut button = CreateButton::new(id)
                        .style(button_style)
                        .disabled(disabled);
                    if let Some(emoji) = emoji {
                        button = button.emoji(emoji.reaction());
                    }

                    if label.len() > 0 {
                        button = button.label(label);
                    }

                    self.components = Some(vec![
                        CreateActionRow::Buttons(vec![button])
                    ]);
                }
            }
        }

        return self;
    }

    pub fn primary_button(mut self, _label: impl Into<String>, _id: impl Into<String>, emoji: Option<Emoji>, disabled: bool) -> Self {
        self.button(_label, _id, emoji, disabled, ButtonStyle::Primary)
    }

    pub fn secondary_button(mut self, _label: impl Into<String>, _id: impl Into<String>, emoji: Option<Emoji>, disabled: bool) -> Self {
        self.button(_label, _id, emoji, disabled, ButtonStyle::Secondary)
    }

    pub fn success_button(mut self, _label: impl Into<String>, _id: impl Into<String>, emoji: Option<Emoji>, disabled: bool) -> Self {
        self.button(_label, _id, emoji, disabled, ButtonStyle::Success)
    }

    pub fn danger_button(mut self, _label: impl Into<String>, _id: impl Into<String>, emoji: Option<Emoji>, disabled: bool) -> Self {
        self.button(_label, _id, emoji, disabled, ButtonStyle::Danger)
    }

    /*pub fn attachments(mut self, attachments: EditAttachments) -> Self {
        self.attachments = attachments;
        self
    }

    pub fn attachment(mut self, attachment: EditAttachments) -> Self {
        self.attachments = attachment;
        self
    }*/

    pub fn add_file(mut self, file: CreateAttachment) -> Self {
        self.attachments = self.attachments
            .into_iter()
            .chain(std::iter::once(file))
            .collect();
        self
    }

    pub fn add_files(mut self, files: impl IntoIterator<Item = CreateAttachment>) -> Self {
        self.attachments = self.attachments
            .into_iter()
            .chain(files)
            .collect();
        self
    }

    pub async fn exec(self, http_cache: impl CacheHttp) -> skua::Result<()> {

        if self.content.is_none() && self.embeds.is_none() && self.components.is_none() {
            return Err(skua::Error::Other("No content, embeds, or components provided"));
        }

        return if self.deferred {
            let mut builder = CreateInteractionResponseFollowup::default();

            if let Some(content) = self.content {
                builder = builder.content(content);
            }
            if let Some(tts) = self.tts {
                builder = builder.tts(tts);
            }
            if let Some(embeds) = self.embeds {
                builder = builder.embeds(embeds);
            }
            if let Some(allowed_mentions) = self.allowed_mentions {
                builder = builder.allowed_mentions(allowed_mentions);
            }
            if let Some(components) = self.components {
                builder = builder.components(components);
            }

            builder = builder.add_files(self.attachments);

            builder.execute(http_cache, (None, &self.token)).await?;
            Ok(())
        } else {

            let mut message = CreateInteractionResponseMessage::default();
            if let Some(content) = self.content {
                message = message.content(content);
            }
            if let Some(tts) = self.tts {
                message = message.tts(tts);
            }
            if let Some(embeds) = self.embeds {
                message = message.embeds(embeds);
            }
            if let Some(allowed_mentions) = self.allowed_mentions {
                message = message.allowed_mentions(allowed_mentions);
            }
            if let Some(flags) = self.flags {
                message = message.flags(flags);
            }
            if let Some(components) = self.components {
                message = message.components(components);
            }

            message = message.add_files(self.attachments);

            let builder = CreateInteractionResponse::Message(message);
            builder.execute(http_cache, (self.id, &self.token)).await
        }
    }
}