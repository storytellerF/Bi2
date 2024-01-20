package com.storyteller_f.bi.entity.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class VideoRelateInfo(
    @SerialName("ad_index") val adIndex: Int,
    val aid: String?,
    @SerialName("card_index") val cardIndex: Int,
    val cid: Double,

    val duration: Int,
    val goto: String,
    @SerialName("is_ad_loc") val isAdLoc: Boolean,
    val owner: VideoOwnerInfo?,
    val `param`: String,
    val pic: String,

    @SerialName("src_id") val srcId: Int,
    val stat: VideoStatInfo?,
    val title: String,
    val trackid: String,
    val uri: String,
)