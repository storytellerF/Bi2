package com.storyteller_f.bi.entity.bangumi

import kotlinx.serialization.Serializable

@Serializable
data class BangumiRatingInfo(
    val count: Int,
    val score: Double,
)
