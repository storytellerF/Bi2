package com.storyteller_f.bi.entity.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchArchiveInfo(
    var archive: List<SearchVideoInfo>?,
)