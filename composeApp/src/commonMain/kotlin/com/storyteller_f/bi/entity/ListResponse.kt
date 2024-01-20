package com.storyteller_f.bi.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListResponse<T>(
    val count: Int,
    val list: List<T>,
    @SerialName("has_more") val hasMore: Boolean,
)
