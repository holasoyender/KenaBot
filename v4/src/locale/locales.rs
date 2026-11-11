/*
 * File:          locales.rs
 * Path:          v4\src\locale\locales.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-14 17:27:28 +01:00
 *
 * SHA-256: 4eee108d677b3a10f276e201a9d5faaee4fae12dd57fb6fb9d3bbe9b8ddbd8ed
 *
 */


#[allow(non_camel_case_types)]
#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
pub enum Locales {
    SPANISH,
    ENGLISH_US,
    ENGLISH_UK,
    FRENCH,
    PORTUGUESE_BRAZILIAN,
    ITALIAN,
    GERMAN,
    UKRAINIAN,
}

//https://discord.com/developers/docs/reference#locales
impl Locales {
    pub fn value(&self) -> &'static str {
        match *self {
            Locales::SPANISH => "es-ES",
            Locales::ENGLISH_US => "en-US",
            Locales::ENGLISH_UK => "en-GB",
            Locales::FRENCH => "fr",
            Locales::PORTUGUESE_BRAZILIAN => "pt-BR",
            Locales::ITALIAN => "it",
            Locales::GERMAN => "de",
            Locales::UKRAINIAN => "uk",
        }
    }

    pub fn from_str(value: &str) -> Option<Locales> {
        match value {
            "es-ES" => Some(Locales::SPANISH),
            "en-US" => Some(Locales::ENGLISH_US),
            "en-GB" => Some(Locales::ENGLISH_UK),
            "fr" => Some(Locales::FRENCH),
            "pt-BR" => Some(Locales::PORTUGUESE_BRAZILIAN),
            "it" => Some(Locales::ITALIAN),
            "de" => Some(Locales::GERMAN),
            "uk" => Some(Locales::UKRAINIAN),
            _ => None,
        }
    }

    pub fn from_str_or_default(value: &str) -> Locales {
        match value {
            "es-ES" => Locales::SPANISH,
            "en-US" => Locales::ENGLISH_US,
            "en-GB" => Locales::ENGLISH_UK,
            "fr" => Locales::FRENCH,
            "pt-BR" => Locales::PORTUGUESE_BRAZILIAN,
            "it" => Locales::ITALIAN,
            "de" => Locales::GERMAN,
            "uk" => Locales::UKRAINIAN,
            _ => Locales::SPANISH,
        }
    }

    pub fn name(&self) -> &'static str {
        match *self {
            Locales::SPANISH => "Español",
            Locales::ENGLISH_US => "English (US)",
            Locales::ENGLISH_UK => "English (UK)",
            Locales::FRENCH => "Français",
            Locales::PORTUGUESE_BRAZILIAN => "Português",
            Locales::ITALIAN => "Italiano",
            Locales::GERMAN => "Deutsch",
            Locales::UKRAINIAN => "Українська",
        }
    }
}