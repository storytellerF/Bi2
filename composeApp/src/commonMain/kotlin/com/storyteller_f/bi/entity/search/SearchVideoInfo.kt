package com.storyteller_f.bi.entity.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchVideoInfo(
    val author: String,
    val cover: String,
    val danmaku: String,
    val desc: String,
    val duration: String,
    val goto: String,
    val mid: String,
    val `param`: String,
    val play: String,
    val status: Int,
    val title: String,
    @SerialName("total_count") val totalCount: Long,
    val uri: String,
)