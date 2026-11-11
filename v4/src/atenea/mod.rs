/*
 * File:          mod.rs
 * Path:          v4\src\atenea\mod.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-27 16:37:33 +01:00
 *
 * SHA-256: f5e04f9311bb77152d980fdbb11be1059c23ea9b3b0f3c1481230ac845619c5d
 *
 */


#![allow(clippy::type_complexity)]
#![allow(rustdoc::bare_urls)]

/// The main client, where everything gets done.
pub mod client;
/// Every possible error that the library can return.
pub mod error;
/// The REST API.
pub mod http;
/// Mappings of objects received or sent from or to the API.
pub mod model;
/// A Atenea server connection.
pub mod node;
/// Player related methods.
pub mod player_context;
/// Re-exports of all the most common types.
pub use typemap_rev;