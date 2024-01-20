package com.storyteller_f.bi.entity.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class VideoStatInfo(
    val aid: String,
    var coin: Int,
    val danmaku: String,
    val dislike: Int,
    var favorite: Int,
    @SerialName("his_rank") val hisRank: Int,
    var like: Int,
    @SerialName("now_rank") val nowRank: Int,
    val reply: Int,
    val share: Int,
    val view: String
) {
    constructor(danmaku: String, view: String) : this(
        "", 0, danmaku,
        0, 0, 0, 0,
        0, 0, 0, view
    )
}