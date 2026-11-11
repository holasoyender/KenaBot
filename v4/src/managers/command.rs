/*
 * File:          command.rs
 * Path:          v4\src\managers\command.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-03-10 19:48:01 +01:00
 *
 * SHA-256: 4604e06f8b82b1cbbded1c0e83fa67ff521b32a9af8346008f23fb9af4398aec
 *
 */


use std::collections::HashMap;
use std::sync::Arc;
use async_trait::async_trait;
use rocket::serde::{Serialize};
use skua::all::{Interaction, Context, CreateCommand, Permissions};
use skua::all::standard::CommandError;
use tokio::sync::Mutex;
use crate::commands;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::locale::language::Language;
use crate::managers::interactions::KenaCommandInteraction;

//type Command = dyn Fn(Context, Interaction, CommandInteraction) -> Result<(), CommandError> + Send + Sync;

#[async_trait]
pub trait Command {
    async fn run(
        &mut self,
        context: Context,
        interaction: Interaction,
        command_interaction: KenaCommandInteraction,
        config: Guild,
        lang: Language,
        event_handler: &Handler
    ) -> Result<(), CommandError>;
    fn register(&self) -> CreateCommand;

    fn is_premium(&self) -> bool;
    fn permissions(&self) -> Vec<Permissions>;
}

#[derive(Clone)]
pub struct CommandManager {
    //pub commands: HashMap<String, Box<dyn Command + Send + Sync>>
    pub commands: HashMap<String, Arc<Mutex<dyn Command + Send + Sync>>>
}

#[derive(Debug, Clone, Serialize)]
pub struct CommandMetadata {
    pub name: String,
    pub descriptions: HashMap<String, String>,
    pub permissions: Vec<Permissions>,
    pub premium: bool
}

impl CommandManager {
    //create a new command manager and run load_commands
    pub fn new() -> Self {
        let mut command_manager = CommandManager {
            commands: HashMap::new()
        };
        command_manager.load_commands();
        command_manager
    }

    pub async fn get_metadata(&self) -> Vec<CommandMetadata> {
        let mut metadata_list = Vec::new();
        for (name, command) in &self.commands {
            let command = command.lock().await;

            let descriptions = command.register().description_localizations;

            let metadata = CommandMetadata {
                name: name.clone(),
                descriptions,
                permissions: command.permissions(),
                premium: command.is_premium()
            };
            metadata_list.push(metadata);
        }
        metadata_list
    }

    pub fn load_commands(&mut self) {
        self.commands.insert("ping".to_string(), Arc::new(Mutex::new(commands::ping::PingCommand)));
        self.commands.insert("invite".to_string(), Arc::new(Mutex::new(commands::invite::InviteCommand)));
        self.commands.insert("help".to_string(), Arc::new(Mutex::new(commands::help::HelpCommand)));
        self.commands.insert("support".to_string(), Arc::new(Mutex::new(commands::support::SupportCommand)));
        self.commands.insert("commands".to_string(), Arc::new(Mutex::new(commands::commands::CommandsCommand)));
        self.commands.insert("autoplay".to_string(), Arc::new(Mutex::new(commands::autoplay::AutoplayCommand)));
        self.commands.insert("artist".to_string(), Arc::new(Mutex::new(commands::artist::ArtistCommand)));
        self.commands.insert("247".to_string(), Arc::new(Mutex::new(commands::radio::RadioCommand)));
        self.commands.insert("play".to_string(), Arc::new(Mutex::new(commands::play::PlayCommand)));
        self.commands.insert("skip".to_string(), Arc::new(Mutex::new(commands::skip::SkipCommand)));
        self.commands.insert("stop".to_string(), Arc::new(Mutex::new(commands::stop::StopCommand)));
        self.commands.insert("pause".to_string(), Arc::new(Mutex::new(commands::pause::PauseCommand)));
        self.commands.insert("resume".to_string(), Arc::new(Mutex::new(commands::resume::ResumeCommand)));
        self.commands.insert("clearqueue".to_string(), Arc::new(Mutex::new(commands::clear_queue::ClearQueueCommand)));
        self.commands.insert("leave".to_string(), Arc::new(Mutex::new(commands::leave::LeaveCommand)));
        self.commands.insert("volume".to_string(), Arc::new(Mutex::new(commands::volume::VolumeCommand)));
        self.commands.insert("skipto".to_string(), Arc::new(Mutex::new(commands::skip_to::SkipToCommand)));
        self.commands.insert("queue".to_string(), Arc::new(Mutex::new(commands::queue::QueueCommand)));
        self.commands.insert("remove".to_string(), Arc::new(Mutex::new(commands::remove::RemoveCommand)));
        self.commands.insert("nowplaying".to_string(), Arc::new(Mutex::new(commands::now_playing::NowPlayingCommand)));
        self.commands.insert("back".to_string(), Arc::new(Mutex::new(commands::back::BackCommand)));
        self.commands.insert("seek".to_string(), Arc::new(Mutex::new(commands::seek::SeekCommand)));
        self.commands.insert("config".to_string(), Arc::new(Mutex::new(commands::config::ConfigCommand)));
        self.commands.insert("shuffle".to_string(), Arc::new(Mutex::new(commands::shuffle::ShuffleCommand)));
        self.commands.insert("loop".to_string(), Arc::new(Mutex::new(commands::repeat::LoopCommand)));
        self.commands.insert("filter".to_string(), Arc::new(Mutex::new(commands::filter::FilterCommand)));
    }
}