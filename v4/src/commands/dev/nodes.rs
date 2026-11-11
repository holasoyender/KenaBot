/*
 * File:          nodes.rs
 * Path:          v4\src\commands\dev\nodes.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: 5a1773d5d2c9635bfd23ad0c53874cc6140b6c862d2d5422be0a6ef203ff35cf
 *
 */


use async_trait::async_trait;
use futures::StreamExt;
use skua::all::{Context, CreateEmbed, CreateEmbedAuthor, CreateEmbedFooter, CreateMessage, Message};
use skua::all::standard::CommandError;
use shared::emojis;
use crate::atenea::model::events::FrameStats;
use crate::database::cache::node::get_all_node_stats;
use crate::discord::Handler;
use crate::managers::dev_command::DevCommand;
use crate::util::chunk_hashmap;
use crate::util::time::format_duration;

pub(crate) struct DevNodesCommand;

#[async_trait]
impl DevCommand for DevNodesCommand {
    async fn run(&mut self, context: Context, message: Message, _: Vec<&str>, event_handler: &Handler) -> Result<(), CommandError> {
        let bot_id = context.cache.current_user().id;
        let nodes = get_all_node_stats(bot_id, &event_handler.cache).await;

        if nodes.is_empty() {
            message.reply(&context.http, "No se encontraron nodos de voz").await?;
            return Ok(());
        }
        
        let chunked = chunk_hashmap(nodes, 9);
        for (index, chunk) in chunked.iter().enumerate() {
            
            let chunk = chunk.to_owned();
            if index == 0 {

                let mut embed = CreateEmbed::new()
                    .color(0x2f3136)
                    .author(CreateEmbedAuthor::new("Información de los nodos de Atenea").icon_url(context.cache.current_user().avatar_url().unwrap()))
                    .description("Estos nodos son los que se encargan de reproducir la música en los servidores")
                    .footer(CreateEmbedFooter::new(format!("Página 1 de {}", chunked.len())));
                
                for node in chunk {
                    let id = node.0;
                    let stats = node.1;

                    let frame_stats = stats.frame_stats.unwrap_or_else(|| FrameStats { sent: 0, nulled: 0, deficit: 0 });

                    embed = embed.field(
                        format!("{} Nodo '{}'", emojis::EMOJIS.online.normal(), id),
                        format!("```Load: {}% ({}% total)\nMemory: {}mb/{}mb\nUptime: {}\nPlayers: {}/{}\nFrames/minute: {} ({} nulled, {} deficit)```",
                                (stats.cpu.atenea_load * 100.0).floor(),
                                (stats.cpu.system_load * 100.0).floor(),
                                stats.memory.used / 1024 / 1024,
                                stats.memory.allocated / 1024 / 1024,
                                format_duration(stats.uptime),
                                stats.playing_players,
                                stats.players,
                                frame_stats.sent,
                                frame_stats.nulled,
                                frame_stats.deficit
                        ),
                        true
                    );

                    message.channel_id.send_message(&context.http,
                                                    CreateMessage::new()
                                                        .embed(embed.clone())).await?;
                }
            } else {
                let mut n_embed = CreateEmbed::new()
                    .color(0x2f3136)
                    .footer(CreateEmbedFooter::new(format!("Página {} de {}", index, chunked.len())));

                for node in chunk {
                    let id = node.0;
                    let stats = node.1;

                    let frame_stats = stats.frame_stats.unwrap_or_else(|| FrameStats { sent: 0, nulled: 0, deficit: 0 });

                    n_embed = n_embed.field(
                        format!("{} Nodo '{}'", emojis::EMOJIS.online.normal(), id),
                        format!("```Load: {}% ({}% total)\nMemory: {}mb/{}mb\nUptime: {}\nPlayers: {}/{}\nFrames/minute: {} ({} nulled, {} deficit)```",
                                (stats.cpu.atenea_load * 100.0).floor(),
                                (stats.cpu.system_load * 100.0).floor(),
                                stats.memory.used / 1024 / 1024,
                                stats.memory.allocated / 1024 / 1024,
                                format_duration(stats.uptime),
                                stats.playing_players,
                                stats.players,
                                frame_stats.sent,
                                frame_stats.nulled,
                                frame_stats.deficit
                        ),
                        true
                    );

                    message.channel_id.send_message(&context.http,
                                                    CreateMessage::new()
                                                        .embed(n_embed.clone())).await?;
                
                }
            }
            
        }

        Ok(())
    }

    fn name(&self) -> &str {
        "nodes"
    }

    fn description(&self) -> &str {
        "Información sobre los nodos de voz del bot"
    }
}