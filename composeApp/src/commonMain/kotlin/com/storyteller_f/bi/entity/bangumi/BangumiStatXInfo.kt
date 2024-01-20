package com.storyteller_f.bi.entity.bangumi

import kotlinx.serialization.Serializable

@Serializable
data class BangumiStatXInfo(
    val coins: String,
    val danmakus: String,
    val favorites: String,
    val reply: String,
    val share: String,
    val views: String,
)
