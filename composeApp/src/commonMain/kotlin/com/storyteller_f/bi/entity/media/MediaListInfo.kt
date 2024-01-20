package com.storyteller_f.bi.entity.media

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaListInfo(
    var cover: String,
    @SerialName("cover_type") val coverType: Int,
    var ctime: Long,
    @SerialName("fav_state") val favState: Int,
    var fid: Long,
    var id: String,
    var intro: String,
    @SerialName("like_state") val likeState: Int,
    @SerialName("media_count") val mediaCount: Int,
    var mid: Long,
    var mtime: Long,
    var state: Int,
    var title: String,
    var type: Int,
)
