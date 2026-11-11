/*
 * File:          dev_command.rs
 * Path:          v4\src\managers\dev_command.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: 813ce3da4dd64da81274c3a4b7f8bfc830016a832f02ea7e24222a9194b82cf3
 *
 */


use std::collections::HashMap;
use std::sync::Arc;
use async_trait::async_trait;
use skua::all::{Context, Message};
use skua::all::standard::CommandError;
use tokio::sync::Mutex;
use crate::commands::dev;
use crate::discord::Handler;

#[async_trait]
pub trait DevCommand {
    async fn run(
        &mut self,
        context: Context,
        message: Message,
        args: Vec<&str>,
        event_handler: &Handler
    ) -> Result<(), CommandError>;
    fn name(&self) -> &str;
    fn description(&self) -> &str;
}

#[derive(Clone)]
pub struct DevCommandManager {
    pub commands: HashMap<String, Arc<Mutex<dyn DevCommand + Send + Sync>>>
}

impl DevCommandManager {
    //create a new command manager and run load_commands
    pub fn new() -> Self {
        let mut command_manager = DevCommandManager {
            commands: HashMap::new()
        };
        command_manager.load_commands();
        command_manager
    }

    pub fn load_commands(&mut self) {
        self.commands.insert("ping".to_string(), Arc::new(Mutex::new(dev::ping::DevPingCommand)));
        self.commands.insert("nodes".to_string(), Arc::new(Mutex::new(dev::nodes::DevNodesCommand)));
    }
}