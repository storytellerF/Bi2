package com.storyteller_f.bi.entity.bangumi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewestEpisodeInfo(
    val desc: String,
    val id: Int,
    val index: String,
    @SerialName("is_new") val isNew: Int,
    @SerialName("pub_real_time") val pubRealTime: String,
)