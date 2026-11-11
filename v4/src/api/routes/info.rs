/*
 * File:          info.rs
 * Path:          v4\src\api\routes\info.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 19:54:39 +01:00
 *
 * SHA-256: 774ed27293d17a11c940d483e308d64c8edab304caee51e5ffdf5463247c11ab
 *
 */


use rocket::get;
use rocket::http::Status;
use rocket::serde::json::Json;
use serde_json::json;
use crate::api::util::ip::RemoteAddress;
use crate::api::util::responses::{DataResponse, Requester};
use crate::locale::manager::LanguageManager;
use crate::managers::command::CommandManager;

#[get("/data")]
pub async fn get_data(lang_manager: &rocket::State<LanguageManager>, command_manager: &rocket::State<CommandManager>, remote: RemoteAddress) -> Result<Json<DataResponse>, Status> {

    let lang_manager = lang_manager as &LanguageManager;
    let command_manager = command_manager as &CommandManager;

    let lang_metadata = lang_manager.get_metadata();
    let command_metadata = command_manager.get_metadata().await;

    let response = DataResponse {
        data: json!({
            "languages": serde_json::to_value(lang_metadata).unwrap(),
            "commands": serde_json::to_value(command_metadata).unwrap()
        }),
        requester: Requester {
            ip: remote.ip,
            user_agent: remote.user_agent
        },
        status: 200
    };

    Ok(Json(response))
}