package com.storyteller_f.bi.entity

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

@Serializable
data class MediasInfo(
    @SerialName("cnt_info") val cntInfo: CntInfo,
    val cover: String,
    val ctime: Long,
    val id: String,
    val title: String,
    val upper: UpperInfo,
) {

    @Serializable
    data class CntInfo(
        val coin: Int,
        val collect: Int,
        val danmaku: String,
        val play: String,
        val reply: Int,
        val share: Int,
        @SerialName("thumb_down") val thumbDown: Int,
        @SerialName("thumb_up") val thumbUp: Int
    )

    @Serializable
    data class UpperInfo(
        val face: String,
        val name: String,
        val mid: String
    )
}

@Serializable
data class MediaDetailInfo(
    val medias: List<MediasInfo>?,
)
