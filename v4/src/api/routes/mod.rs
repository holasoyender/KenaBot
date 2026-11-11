/*
 * File:          mod.rs
 * Path:          v4\src\api\routes\mod.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 19:54:39 +01:00
 *
 * SHA-256: dbbec7b5cbd1e8b0d6c3201363243429a30ec4b50486caf093991be82d5e7406
 *
 */


mod errors;
mod status;
mod info;
mod blocked;

use rocket::{catchers, routes};

pub fn catchers() -> Vec<rocket::Catcher> {
    return catchers![
        errors::not_found,
        errors::internal_error,
        errors::forbidden,
        errors::unauthorized,
    ];
}

pub fn common() -> Vec<rocket::Route> {
    return routes![
        status::get_status,
        info::get_data,
        blocked::get_blocked,
    ];
}