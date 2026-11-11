/*
 * File:          string.rs
 * Path:          v4\src\util\string.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-19 23:43:04 +01:00
 *
 * SHA-256: 1177ed5e074a50a6c28786d3d441833f70473eaccf054a0b5248d24e2c8539cd
 *
 */


use url::Url;

pub fn format_song_title(title: &String) -> String {
    let mut title = title.to_string();
    title = regex::Regex::new(r"\(.*?\)").unwrap().replace_all(&title, "").to_string();
    title = regex::Regex::new(r"\{.*?}").unwrap().replace_all(&title, "").to_string();
    title = regex::Regex::new(r"\[.*?]").unwrap().replace_all(&title, "").to_string();
    title = regex::Regex::new(r"<.*?>").unwrap().replace_all(&title, "").to_string();
    title = title.replace("lyric", "");
    title = title.replace("lyrics", "");
    title = title.replace("music", "");
    title = title.replace("video", "");
    title = title.replace("**", "*\\*");

    if title.len() == 0 {
        title = "Unknown".to_string();
    }

    title.trim().to_string()
}

pub fn player_timestamp(milliseconds: &u64) -> String {
    let seconds = ((milliseconds / 1000) % 60) as i64;
    let minutes = ((milliseconds / (1000 * 60)) % 60) as i64;
    let hours = ((milliseconds / (1000 * 60 * 60)) % 24) as i64;

    if hours > 0 {
        format!("{:02}:{:02}:{:02}", hours, minutes, seconds)
    } else {
        format!("{:02}:{:02}", minutes, seconds)
    }
}

pub fn is_url(input: &String) -> bool {
    Url::parse(input).is_ok()
}

pub fn progress(__total: &u64, __current: &u64, size: u64, line: &str, slider: &str) -> String {
    let total = *__total;
    let current = *__current;

    return if current > total {
        line.repeat((size + 2) as usize)
    } else {
        let percentage = current as f64 / total as f64;
        let progress = (percentage * size as f64).round() as u64;
        let empty_progress = size - progress;
        let progress_text = line.repeat(progress as usize) + slider;
        let empty_progress_text = line.repeat(empty_progress as usize);
        format!("{}{}", progress_text, empty_progress_text)
    }
}