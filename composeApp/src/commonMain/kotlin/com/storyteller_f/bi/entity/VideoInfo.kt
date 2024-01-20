package com.storyteller_f.bi.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoInfo(
    val aid: Long,
    val bvid: String,
    val cid: Long,
    val ctime: Double,
    val desc: String,
    val duration: Long,
    val pages: List<VideoPageInfo>,
    val pic: String,
    val pubdate: Long,
    val tag: List<VideoTagInfo>,
    val title: String
)

@Serializable
data class VideoTagInfo(
    @SerialName("tag_id") val tagId: Double,
    @SerialName("tag_name") val tagName: String
)

@Serializable
data class VideoPageInfo(
    val page: Int,
    val part: String
)

@Serializable
data class VideoDatum(
    val aid: Long,
    val bvid: String,
    val cid: Int,
    val name: String? = null,
    val pic: String,
    val progress: Int,
    val tid: Long,
    val title: String,
)

@Serializable
class VideoDatumList(
    val count: Int,
    val list: List<VideoDatum>,
)
