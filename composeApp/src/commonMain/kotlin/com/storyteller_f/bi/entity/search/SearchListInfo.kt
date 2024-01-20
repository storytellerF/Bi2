package com.storyteller_f.bi.entity.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchListInfo<T>(
    var items: List<T>?,
    var pages: Int,
)
