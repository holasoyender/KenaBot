/*
 * File:          manager.rs
 * Path:          v4\src\locale\manager.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 6af2c7a5217df674d0962c539f24753df63761d80ca59f29df1835870f2d3c01
 *
 */


use std::collections::HashMap;
use serde::{Serialize, Deserialize};
use crate::locale::language::Language;
use crate::locale::locales::Locales;

#[derive(Debug, Clone)]
pub struct LanguageManager {
    languages: HashMap<Locales, Language>
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct LanguageMetadata {
    pub name: String,
    pub code: String,
    pub description: String,
    pub flag: String
}

impl LanguageManager {
    pub fn new() -> Self {
        let mut language_manager = LanguageManager {
            languages: HashMap::new()
        };
        language_manager.load_languages();
        language_manager
    }

    pub fn load_languages(&mut self) {

        let lang_folder = std::path::Path::new("locales");
        if !lang_folder.exists() {
            std::fs::create_dir(lang_folder).unwrap();
            error!("The 'locales' folder does not exist, please create it and add the language files");
            std::process::exit(1);
        }

        let paths = std::fs::read_dir(lang_folder).unwrap();
        for path in paths {
            match path {
                Ok(path) => {
                    let path = path.path();
                    if path.is_file() {
                        let file_name = path.file_name().unwrap().to_str().unwrap();
                        let lang_name = file_name.replace(".json", "");
                        if file_name.ends_with(".json") {
                            let locale = Locales::from_str(lang_name.as_str());
                            match locale {
                                Some(locale) => {
                                    let language = Language::load_from_file(&path);
                                    match language {
                                        Ok(language) => {
                                            info!("Loaded language file for '{}' ({})", lang_name, language.name);
                                            self.languages.insert(locale, language);
                                        },
                                        Err(err) => {
                                            warn!("The language file {} is not a valid JSON file, please remove it: {}", file_name, err);
                                            continue
                                        }
                                    }
                                },
                                None => {
                                    warn!("The language file {} is not in the allowed language list, please add it or remove the file", file_name);
                                    continue
                                }
                            }
                        } else {
                            warn!("The language file {} is not a valid JSON file, please remove it", file_name);
                            continue
                        }
                    } else {
                        warn!("The lang folder contains a folder, please remove it");
                        continue
                    }
                },
                Err(_) => {
                    warn!("Could not load a language file");
                    continue
                }
            }
        }

    }

    pub fn get_language(&self, locale: &Locales) -> Option<&Language> {
        self.languages.get(locale)
    }

    pub fn get_language_or_default(&self, locale: &Locales) -> &Language {
        self.get_language(locale).unwrap_or(&self.languages[&Locales::SPANISH])
    }

    pub fn get_language_or_default_str(&self, locale: &str) -> &Language {
        self.get_language_or_default(&Locales::from_str_or_default(locale))
    }

    pub fn get_metadata(&self) -> Vec<LanguageMetadata> {
        self.languages.iter().map(|(locale, language)| {
            LanguageMetadata {
                name: language.name.clone(),
                code: locale.value().to_string(),
                description: language.description.clone(),
                flag: language.flag.clone()
            }
        }).collect()
    }
}