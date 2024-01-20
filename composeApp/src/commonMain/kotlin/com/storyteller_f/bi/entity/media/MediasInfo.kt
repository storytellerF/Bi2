package com.storyteller_f.bi.entity.media

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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