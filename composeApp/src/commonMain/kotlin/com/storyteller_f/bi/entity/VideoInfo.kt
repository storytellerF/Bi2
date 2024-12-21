package com.storyteller_f.bi.entity

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

@Serializable
data class VideoTagInfo(
    val attribute: Int,
    val cover: String,
    val hated: Int,
    val hates: Int,
    @SerialName("is_activity") val isActivity: Int,
    val liked: Int,
    val likes: Int,
    @SerialName("tag_id") val tagId: Double,
    @SerialName("tag_name") val tagName: String
)

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

@Serializable
data class VideoStaffInfo(
    var mid: String,
    var title: String,
    var face: String,
    var name: String
)

@Serializable
data class VideoReqUserInfo(
    val attention: Int,
    var coin: Int?,
    var dislike: Int?,
    var favorite: Int?,
    var like: Int?
)

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

@Serializable
data class VideoPageInfo(
    val cid: String,

    val dmlink: String,
    val duration: Int,
    val from: String,

    val page: Int,
    var part: String,
    val vid: String,
    val weblink: String
)

@Serializable
data class VideoOwnerInfo(
    val face: String,
    val mid: String,
    val name: String
)

@Serializable
data class VideoDatum(
    val aid: Long,
    val bvid: String,
    val cid: Int,
    val copyright: Int,
    val count: Int,
    val desc: String,
    val duration: Int,
    val dynamic: String,
    val name: String,
    val pic: String,
    val progress: Int,
    val state: Int,
    /**
     * 分区id
     */
    val tid: Long,
    val title: String,
    val videos: Int,
)

@Serializable
class VideoDatumList(
    val count: Int,
    val list: List<VideoDatum>,
)
