package com.storyteller_f.bi.entity.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchUpperInfo(
    var archives: Int,
    var cover: String,
    var fans: Int,
    var goto: String,
    var param: String,
    var sign: String,
    var status: Int,
    var title: String,
    @SerialName("total_count") val totalCount: Int,
    var uri: String,
)