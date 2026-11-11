/*
 * File:          errors.rs
 * Path:          v4\src\api\routes\errors.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-25 18:57:51 +01:00
 *
 * SHA-256: 582a68ff45130191e3be443c9bea4c3d2a019d41b7a551ac09ad841a3add1157
 *
 */


use rocket::http::Status;
use rocket::{catch, Request};
use rocket::serde::json::Json;
use crate::api::util::responses::GenericResponse;

#[catch(401)]
pub fn unauthorized() -> Json<GenericResponse> {
    let response_json = GenericResponse {
        status: 401,
        message: "Unauthorized".to_string(),
    };
    Json(response_json)
}

#[catch(403)]
pub fn forbidden() -> Json<GenericResponse> {
    let response_json = GenericResponse {
        status: 403,
        message: "You are forbidden from accessing this resource. You can check your blacklist status at /api/blacklist/check".to_string(),
    };
    Json(response_json)
}

#[catch(404)]
pub fn not_found() -> Json<GenericResponse> {
    let response_json = GenericResponse {
        status: 404,
        message: "Not found".to_string(),
    };
    Json(response_json)
}

#[catch(default)]
pub fn internal_error(status: Status, request: &Request) -> Json<GenericResponse> {
    let response_json = GenericResponse {
        status: status.code as i32,
        message: format!("{}: '{}'", status, request.uri()),
    };
    Json(response_json)
}