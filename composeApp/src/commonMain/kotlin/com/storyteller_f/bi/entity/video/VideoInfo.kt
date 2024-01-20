package com.storyteller_f.bi.entity.video


import com.storyteller_f.bi.entity.bangumi.SeasonInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class VideoInfo(
    val aid: String,
    val attribute: Int,
    val bvid: String,
    val cid: Long,


    val copyright: Int,
    val ctime: Double,
    var desc: String,


    @SerialName("dm_seg") val dmSeg: Int,
    val duration: Int,
    val `dynamic`: String,

    val owner: VideoOwnerInfo,

    val pages: List<VideoPageInfo>,
    val pic: String,
    val pubdate: Long,
    val relates: List<VideoRelateInfo>?,
    @SerialName("req_user") val reqUser: VideoReqUserInfo,

    val season: SeasonInfo?,
    val staff: List<VideoStaffInfo>?,
    var stat: VideoStatInfo,
    val state: Int,
    val tag: List<VideoTagInfo>,
    val tid: Int,
    val title: String,
    val tname: String,
    val videos: Int,
    @SerialName("view_at") val viewAt: Long? // 历史记录的观看时间
)