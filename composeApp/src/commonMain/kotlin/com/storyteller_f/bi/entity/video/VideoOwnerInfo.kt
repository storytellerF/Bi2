package com.storyteller_f.bi.entity.video

import kotlinx.serialization.Serializable


@Serializable
data class VideoOwnerInfo(
    val face: String,
    val mid: String,
    val name: String
)