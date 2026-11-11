/*
 * File:          logger.rs
 * Path:          v4\src\logger\logger.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 54084347b7d61fdfa10c0162c73aca34bfbe6a10396bac11ef229154e8bbc52b
 *
 */


use fern::colors::{Color, ColoredLevelConfig};
use log::LevelFilter;

pub fn setup_logger() -> Result<(), fern::InitError> {

    std::fs::create_dir_all("logs")?;

    let mut colors = ColoredLevelConfig::new();
    colors.warn = Color::Yellow;
    colors.info = Color::Green;
    colors.error = Color::Red;
    colors.debug = Color::BrightBlack;
    colors.trace = Color::Magenta;

    fern::Dispatch::new()
        .format(move |out, message, record| {
            out.finish(format_args!(
                "{}: {} {} - {}",
                chrono::Local::now().format("%Y-%m-%d %H:%M:%S"),
                record.target(),
                colors.color(record.level()),
                message
            ))
        })
        .level(LevelFilter::Info)
        .level_for("skua", LevelFilter::Warn)
        .level_for("tracing", LevelFilter::Warn)
        .chain(std::io::stdout())
        .chain(fern::log_file("logs/latest.log")?)
        .chain(fern::log_file(format!("logs/{}.log", chrono::Local::now().format("%Y-%m-%d_%H-%M-%S")).as_str())?)
        .apply()?;
    Ok(())
}