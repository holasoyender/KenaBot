/*
 * File:          ip.rs
 * Path:          v4\src\api\util\ip.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-25 18:57:51 +01:00
 *
 * SHA-256: 7b66190c338cc8e368a9e7fc8da5b157182f38e1299010d66176cdf21393d333
 *
 */


use rocket::{Request};
use rocket::request::{FromRequest, Outcome};

pub struct RemoteAddress {
    pub ip: String,
    pub user_agent: Option<String>,
}

#[rocket::async_trait]
impl<'r> FromRequest<'r> for RemoteAddress {
    type Error = ();

    async fn from_request(request: &'r Request<'_>) -> Outcome<Self, Self::Error> {
        let ip = get_ip(request);
        let user_agent = get_user_agent(request);

        Outcome::Success(RemoteAddress {
            ip,
            user_agent
        })
    }
}

pub fn get_ip(request: &Request) -> String {
    let ip = request.client_ip().unwrap().to_string(); // probably 127.0.0.1
    let headers = request.headers();
    let forwarded_for = headers.get_one("X-Forwarded-For");

    match forwarded_for {
        Some(forwarded_for) => {
            let new_ip = match forwarded_for.parse::<std::net::IpAddr>() {
                Ok(new_ip) => new_ip.to_string(),
                Err(_) => return ip
            };
            new_ip
        }
        None => ip
    }
}

fn get_user_agent(request: &Request) -> Option<String> {
    let headers = request.headers();
    let user_agent = headers.get_one("User-Agent");

    return match user_agent {
        Some(user_agent) => Some(user_agent.to_string()),
        None => None
    };
}