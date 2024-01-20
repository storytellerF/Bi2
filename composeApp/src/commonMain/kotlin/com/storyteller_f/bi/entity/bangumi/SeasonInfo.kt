package com.storyteller_f.bi.entity.bangumi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SeasonInfo(
    @SerialName("is_jump") val isJump: Int,
    @SerialName("is_new") val isNew: Int,
    @SerialName("season_id") val seasonId: String,
    @SerialName("season_title") val seasonTitle: String,
    val title: String,
)