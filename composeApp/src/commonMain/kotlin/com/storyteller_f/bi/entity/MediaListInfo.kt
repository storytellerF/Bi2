package com.storyteller_f.bi.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaListInfo(
    val cover: String,
    @SerialName("cover_type") val coverType: Int,
    val ctime: Long,
    @SerialName("fav_state") val favState: Int,
    val fid: Long,
    val id: Int,
    val intro: String,
    @SerialName("media_count") val mediaCount: Int,
    val mid: Long,
    val mtime: Long,
    val state: Int,
    val title: String,
    val type: Int,
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
