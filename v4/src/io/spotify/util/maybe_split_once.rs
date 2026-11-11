/*
 * File:          maybe_split_once.rs
 * Path:          v4\src\io\spotify\util\maybe_split_once.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-22 14:21:17 +01:00
 *
 * SHA-256: 674f2cff619d8b89c6905cc431c8df1c4dcd796a22928a6028d4d863a2bd1865
 *
 */


pub trait MaybeSplitOnce {
    fn maybe_split_once(&self, split: char) -> (&str, Option<&str>);
}

impl MaybeSplitOnce for str {
    fn maybe_split_once(&self, split: char) -> (&str, Option<&str>) {
        let mut split = self.splitn(2, split);

        // splitn(2) returns at most two items, always returning at least one (the entire input)
        let left = split.next().unwrap();
        let right = split.next();

        (left, right)
    }
}
