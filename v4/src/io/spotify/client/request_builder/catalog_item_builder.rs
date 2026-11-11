/*
 * File:          catalog_item_builder.rs
 * Path:          v4\src\io\spotify\client\request_builder\catalog_item_builder.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-22 14:21:17 +01:00
 *
 * SHA-256: aee77f25805bec9a73db8a1bd52e8a393b02991f00f576c1d980df10f9796667
 *
 */


use std::borrow::Cow;

use reqwest::Method;

use crate::io::spotify::{
    client::{
        object,
        request_builder::{BaseRequestBuilderContainer, RequestBuilder},
    },
    model::CountryCode,
};

/// A builder type for catalog searches and item retrievals.
pub struct CatalogItemRequestBuilder<TClient, TResponse, TReturn = TResponse>(
    RequestBuilder<TClient, TResponse, (), TReturn>,
);

impl<TClient, TResponse, TReturn> BaseRequestBuilderContainer<TClient, TResponse, (), TReturn>
    for CatalogItemRequestBuilder<TClient, TResponse, TReturn>
{
    fn new<S>(method: Method, base_url: S, client: TClient) -> Self
    where
        S: Into<Cow<'static, str>>,
    {
        Self(RequestBuilder::new(method, base_url, client))
    }

    fn new_with_body<S>(method: Method, base_url: S, body: (), client: TClient) -> Self
    where
        S: Into<Cow<'static, str>>,
    {
        Self(RequestBuilder::new_with_body(method, base_url, body, client))
    }

    fn take_base_builder(self) -> RequestBuilder<TClient, TResponse, (), TReturn> {
        self.0
    }

    fn get_base_builder_mut(&mut self) -> &mut RequestBuilder<TClient, TResponse, (), TReturn> {
        &mut self.0
    }
}

impl<TReturn, C> CatalogItemRequestBuilder<TReturn, C> {
    /// Specify a target market country for this request. Only content that is available in that market will be returned
    /// and [track relinking](crate::io::spotify::model::track#track-equality-and-track-relinking) may be applied.
    pub fn market(self, market: CountryCode) -> Self {
        self.append_query(object::MARKET_QUERY, market.to_string())
    }
}
