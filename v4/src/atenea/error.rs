/*
 * File:          error.rs
 * Path:          v4\src\atenea\error.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-27 16:37:33 +01:00
 *
 * SHA-256: fd8fbf49e0cb5923564eef9945086212410ab12da5a756423861c2dffc030b90
 *
 */


use crate::atenea::model::track::TrackError;

use std::error::Error;
use std::fmt::{Display, Formatter, Result};

use async_tungstenite::tungstenite::error::Error as TungsteniteError;
use http::Error as HttpError;
use oneshot::RecvError;
use reqwest::{header::InvalidHeaderValue, Error as ReqwestError};
use serde::{Deserialize, Serialize};
use tokio::sync::mpsc::error::SendError;
use url::ParseError;

pub type AteneaResult<T> = std::result::Result<T, AteneaError>;

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
/// Response sent by REST requests when there's an error.
pub struct ResponseError {
    pub status: u16,
    pub timestamp: u64,
    pub error: String,
    pub message: String,
    pub path: String,
    pub trace: Option<String>,
}

#[derive(Serialize, Deserialize)]
#[serde(untagged)]
pub(crate) enum RequestResult<T> {
    Ok(T),
    Err(ResponseError),
}

impl<T> RequestResult<T> {
    pub fn to_result(self) -> std::result::Result<T, ResponseError> {
        match self {
            Self::Ok(x) => Ok(x),
            Self::Err(x) => Err(x),
        }
    }
}

#[derive(Debug)]
/// Every error the library can return.
pub enum AteneaError {
    WebsocketError(TungsteniteError),
    InvalidHeaderValue(InvalidHeaderValue),
    ReqwestError(ReqwestError),
    HttpError(HttpError),
    ChannelSendError,
    ChannelReceiveError(RecvError),
    UrlParseError(ParseError),
    SerdeErrorQs(serde_qs::Error),
    SerdeErrorJson(serde_json::Error),

    ResponseError(ResponseError),
    NoSessionPresent,
    TrackError(TrackError),
}

impl Error for AteneaError {}

impl Display for AteneaError {
    fn fmt(&self, f: &mut Formatter) -> Result {
        match self {
            AteneaError::WebsocketError(why) => {
                write!(
                    f,
                    "Error while sending payload to the websocket => {:?}",
                    why
                )
            }
            AteneaError::InvalidHeaderValue(why) => {
                write!(f, "Invalid Header Value => {:?}", why)
            }
            AteneaError::HttpError(why) => {
                write!(f, "HttpError => {:?}", why)
            }
            AteneaError::ReqwestError(why) => {
                write!(f, "Reqwest Error => {:?}", why)
            }
            AteneaError::ChannelSendError => {
                write!(f, "The channel receiver is closed.")
            }
            AteneaError::ChannelReceiveError(why) => {
                write!(f, "Error receiving from player context: {:?}", why)
            }
            AteneaError::UrlParseError(why) => {
                write!(f, "Url Parsing Error => {:?}", why)
            }
            AteneaError::SerdeErrorQs(why) => {
                write!(f, "Error serializing or desesrializing qs => {:?}", why)
            }
            AteneaError::SerdeErrorJson(why) => {
                write!(f, "Error serializing or desesrializing json => {:?}", why)
            }

            AteneaError::NoSessionPresent => {
                write!(
                    f,
                    "Please, call client.create_session() for this method to work correctly."
                )
            }
            AteneaError::ResponseError(why) => {
                write!(f, "Error from atenea server: {:?}", why)
            }
            AteneaError::TrackError(why) => {
                write!(f, "Error loading tracks: {:?}", why)
            }
        }
    }
}

impl From<ResponseError> for AteneaError {
    fn from(err: ResponseError) -> AteneaError {
        AteneaError::ResponseError(err)
    }
}

impl From<TrackError> for AteneaError {
    fn from(err: TrackError) -> AteneaError {
        AteneaError::TrackError(err)
    }
}

impl From<TungsteniteError> for AteneaError {
    fn from(err: TungsteniteError) -> AteneaError {
        AteneaError::WebsocketError(err)
    }
}

impl From<InvalidHeaderValue> for AteneaError {
    fn from(err: InvalidHeaderValue) -> AteneaError {
        AteneaError::InvalidHeaderValue(err)
    }
}

impl From<HttpError> for AteneaError {
    fn from(err: HttpError) -> AteneaError {
        AteneaError::HttpError(err)
    }
}

impl From<ReqwestError> for AteneaError {
    fn from(err: ReqwestError) -> AteneaError {
        AteneaError::ReqwestError(err)
    }
}

impl From<ParseError> for AteneaError {
    fn from(err: ParseError) -> AteneaError {
        AteneaError::UrlParseError(err)
    }
}

impl<T> From<SendError<T>> for AteneaError {
    fn from(_: SendError<T>) -> AteneaError {
        AteneaError::ChannelSendError
    }
}

impl From<RecvError> for AteneaError {
    fn from(err: RecvError) -> AteneaError {
        AteneaError::ChannelReceiveError(err)
    }
}

impl From<serde_qs::Error> for AteneaError {
    fn from(err: serde_qs::Error) -> Self {
        AteneaError::SerdeErrorQs(err)
    }
}

impl From<serde_json::Error> for AteneaError {
    fn from(err: serde_json::Error) -> Self {
        AteneaError::SerdeErrorJson(err)
    }
}