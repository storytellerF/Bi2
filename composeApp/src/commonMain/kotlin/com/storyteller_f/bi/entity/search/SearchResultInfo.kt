package com.storyteller_f.bi.entity.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultInfo<T>(
    var attribute: Int,
    var items: T,
    var page: Int,
    var trackid: String,
)