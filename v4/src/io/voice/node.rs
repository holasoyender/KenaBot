/*
 * File:          node.rs
 * Path:          v4\src\io\voice\node.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 4e3736f276bc1526e991c325780f1d55d34fe3666c9c10b7b2e285b9820b32f1
 *
 */


use serde::{Deserialize, Serialize};
use skua::all::UserId;
use crate::atenea::model::events;
use crate::atenea::node::NodeBuilder;

#[derive(Debug, Serialize, Deserialize)]
pub struct JsonNode {
    pub host: String,
    pub password: String,
    pub region: String,
    pub name: String
}

#[derive(Debug, Serialize, Deserialize)]
pub struct JsonNodeContainer {
    pub nodes: Vec<JsonNode>,
    pub nodes_dev: Vec<JsonNode>,
    pub nodes_backup: Vec<JsonNode>,
}

pub enum NodeType {
    Normal,
    Dev,
    Backup,
}

pub fn from_json(json: &str, node_type: NodeType) -> Result<Vec<JsonNode>, serde_json::Error> {
    let raw_nodes: JsonNodeContainer = serde_json::from_str(json)?;
    let nodes = match node_type {
        NodeType::Normal => raw_nodes.nodes,
        NodeType::Dev => raw_nodes.nodes_dev,
        NodeType::Backup => raw_nodes.nodes_backup,
    };

    Ok(nodes)
}

pub async fn from_file(path: &str, user_id: UserId, node_type: NodeType) -> Result<Vec<NodeBuilder>, Box<dyn std::error::Error>> {
    let file = tokio::fs::read_to_string(path).await?;
    let nodes = from_json(&file, node_type)?;
    let mut node_builders = Vec::new();

    for node in nodes {

        let node_builder = NodeBuilder {
            hostname: node.host,
            is_ssl: false,
            events: events::Events::default(),
            password: node.password,
            user_id,
            session_id: None,
        };

        node_builders.push(node_builder);
    }

    Ok(node_builders)
}