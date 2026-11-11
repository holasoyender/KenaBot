/*
 * File:          value.rs
 * Path:          v4\src\locale\value.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-19 23:43:04 +01:00
 *
 * SHA-256: 8bd4a0125f1694695e8df1177052a09a065dbdc156dc504a9b27784d8b7d390d
 *
 */



pub(crate) trait Value {
    fn format(&self, args: &[&str]) -> String;
    fn capitalize(&self) -> String;
}

impl Value for String {
    fn format(&self, args: &[&str]) -> String {
        let mut result = String::new();
        let mut iter = self.split("%s");
        let mut pos = 0;

        while let Some(part) = iter.next() {
            result.push_str(part);
            if let Some(arg) = args.get(pos) {
                result.push_str(arg);
            }
            pos += 1;
        }

        result
    }

    fn capitalize(&self) -> String {
        let mut chars = self.chars();
        match chars.next() {
            None => String::new(),
            Some(f) => f.to_uppercase().collect::<String>() + chars.as_str(),
        }
    }
}