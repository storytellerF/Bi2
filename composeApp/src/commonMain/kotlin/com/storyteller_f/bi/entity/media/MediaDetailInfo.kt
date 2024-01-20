package com.storyteller_f.bi.entity.media

import kotlinx.serialization.Serializable

@Serializable
data class MediaDetailInfo(
    val medias: List<MediasInfo>?,
)