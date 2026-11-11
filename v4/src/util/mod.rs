/*
 * File:          mod.rs
 * Path:          v4\src\util\mod.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: a7f2b96f7b9f45e0a8b9172c2563d33dd53dbcc1555de587a31387b7bbae92b8
 *
 */


use std::collections::VecDeque;

pub(crate) mod shards;
pub(crate) mod url;
pub(crate) mod time;
pub(crate) mod string;
pub(crate) mod ms;

pub fn censor<S>(input: S) -> String
    where
        S: Into<String>,
{
    //changue all the characters to * except the first 2 and the last 2
    let input = input.into();
    let len = input.len();
    let mut censored = String::new();

    if len < 5 {
        //censor all the characters except the first one
        for (i, c) in input.chars().enumerate() {
            if i == 0 {
                censored.push(c);
            } else {
                censored.push('*');
            }
        }
        return censored;
    }

    for (i, c) in input.chars().enumerate() {
        if i < 2 || i > len - 3 {
            censored.push(c);
        } else {
            censored.push('*');
        }
    }
    censored
}

pub fn format_number(number: u64) -> String {
    //format the number to be more readable, for example: 1000000 -> 1.000.000
    let mut formatted = String::new();
    let mut counter = 0;

    for c in number.to_string().chars().rev() {
        if counter == 3 {
            formatted.push('.');
            counter = 0;
        }
        formatted.push(c);
        counter += 1;
    }
    formatted.chars().rev().collect::<String>()
}

pub fn chunk_vec<T>(vec: Vec<T>, chunk_size: usize) -> Vec<Vec<T>> {
    //split a vector into multiple vectors with a max size of chunk_size
    let mut chunks = Vec::new();
    let mut chunk = Vec::new();

    for item in vec {
        if chunk.len() == chunk_size {
            chunks.push(chunk);
            chunk = Vec::new();
        }
        chunk.push(item);
    }
    if chunk.len() > 0 {
        chunks.push(chunk);
    }
    chunks
}

pub fn chunk_hashmap<T>(map: std::collections::HashMap<String, T>, chunk_size: usize) -> Vec<std::collections::HashMap<String, T>> {
    //split a hashmap into multiple hashmaps with a max size of chunk_size
    let mut chunks = Vec::new();
    let mut chunk = std::collections::HashMap::new();
    let mut counter = 0;

    for (key, value) in map {
        if counter == chunk_size {
            chunks.push(chunk);
            chunk = std::collections::HashMap::new();
            counter = 0;
        }
        chunk.insert(key, value);
        counter += 1;
    }
    if chunk.len() > 0 {
        chunks.push(chunk);
    }
    chunks
} 

pub fn chunk_vecdeque<T>(vec: VecDeque<T>, chunk_size: usize) -> Vec<Vec<T>> {
    //split a vector into multiple vectors with a max size of chunk_size
    let mut chunks = Vec::new();
    let mut chunk = Vec::new();

    for item in vec {
        if chunk.len() == chunk_size {
            chunks.push(chunk);
            chunk = Vec::new();
        }
        chunk.push(item);
    }
    if chunk.len() > 0 {
        chunks.push(chunk);
    }
    chunks
}