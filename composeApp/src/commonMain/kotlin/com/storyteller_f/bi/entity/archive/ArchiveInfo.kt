package com.storyteller_f.bi.entity.archive

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ArchiveInfo(
    val author: String,
    val bvid: String,
    val cover: String,
    val ctime: Long,
    val danmaku: String,
    val duration: Int,
    @SerialName("first_cid") val firstCid: Int,
    val goto: String,
    @SerialName("icon_type") val iconType: Int,
    @SerialName("is_cooperation") val isCooperation: Boolean,
    @SerialName("is_live_playback") val isLivePlayback: Boolean,
    @SerialName("is_pgc") val isPgc: Boolean,
    @SerialName("is_popular") val isPopular: Boolean,
    @SerialName("is_steins") val isSteins: Boolean,
    @SerialName("is_ugcpay") val isUgcpay: Boolean,
    val length: String,
    val `param`: String,
    val play: String,
    val state: Boolean,
    val subtitle: String,
    val title: String,
    val tname: String,
    @SerialName("ugc_pay") val ugcPay: Int,
    val uri: String,
    val videos: Int,
    @SerialName("view_content") val viewContent: String,
)