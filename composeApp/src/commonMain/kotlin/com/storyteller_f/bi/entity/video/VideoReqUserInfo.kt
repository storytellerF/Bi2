package com.storyteller_f.bi.entity.video

import kotlinx.serialization.Serializable


@Serializable
data class VideoReqUserInfo(
    val attention: Int,
    var coin: Int?,
    var dislike: Int?,
    var favorite: Int?,
    var like: Int?
)
