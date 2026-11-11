/*
 * File:          mod.rs
 * Path:          v4\src\util\url\mod.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-25 18:57:51 +01:00
 *
 * SHA-256: 1c1f1de1e44d93f3a0352140f070c1c5cb8dbc32ef65fffde219b5ac993fa167
 *
 */


mod encode;
pub use encode::encode;
pub use encode::encode_binary;
pub use encode::Encoded;

mod decode;
pub use decode::decode;
pub use decode::decode_binary;