package com.storyteller_f.bi.entity.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class VideoTagInfo(
    val attribute: Int,
    val cover: String,
    val hated: Int,
    val hates: Int,
    @SerialName("is_activity") val isActivity: Int,
    val liked: Int,
    val likes: Int,
    @SerialName("tag_id") val tagId: Double,
    @SerialName("tag_name") val tagName: String
)