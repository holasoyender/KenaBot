/*
 * File:          ms.rs
 * Path:          v4\src\util\ms.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-01-23 17:30:01 +01:00
 *
 * SHA-256: 13984adf19f1aa69de5c3bad431b8de70339472f61100998b36d1e9c16a7d088
 *
 */


pub fn ms(time: &str) -> i64 {
    let mut total_ms: i64 = 0;
    let mut current_value = String::new();

    for ch in time.chars() {
        if ch.is_digit(10) {
            current_value.push(ch);
        } else {
            match ch {
                's' => {
                    total_ms += parse_and_reset(&mut current_value) * 1000;
                }
                'm' => {
                    total_ms += parse_and_reset(&mut current_value) * 60000;
                }
                'h' => {
                    total_ms += parse_and_reset(&mut current_value) * 3600000;
                }
                'd' => {
                    total_ms += parse_and_reset(&mut current_value) * 86400000;
                }
                _ => {
                    // Ignore other characters
                }
            }
        }
    }

    // Parse any remaining value
    total_ms += parse_and_reset(&mut current_value);

    if time.starts_with('-') {
        total_ms *= -1;
    }

    total_ms
}

fn parse_and_reset(current_value: &mut String) -> i64 {
    if current_value.is_empty() {
        return 0;
    }

    match current_value.parse::<i64>() {
        Ok(parsed_value) => {
            // Reset current_value for the next segment
            current_value.clear();
            parsed_value
        }
        Err(_) => 0,
    }
}