package com.storyteller_f.bi.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoData(
    @SerialName("accept_description") val acceptDescription: List<String>,
    @SerialName("accept_format") val acceptFormat: String,
    @SerialName("accept_quality") val acceptQuality: List<Int>,
    val dash: Dash? = null,
    val durl: List<Durl>?,
    val format: String,
    val from: String,
    val message: String,
    val quality: Int,
    val result: String,
) {
    val res: String
        get() = result
}

@Serializable
data class BangumiData(
    @SerialName("accept_description") val acceptDescription: List<String>,
    @SerialName("accept_format") val acceptFormat: String,
    @SerialName("accept_quality") val acceptQuality: List<Int>,
    val code: Int,
    val dash: Dash?,
    val durl: List<Durl>?,
    val format: String,
    val from: String,
    val message: String,
    val quality: Int,
    val result: String,
) {
    val res: String
        get() = result
}

@Serializable
data class Durl(
    val ahead: String,
    val length: Long,
    val order: Int,
    val size: Long,
    val url: String,
    val vhead: String,
)

@Serializable
data class SupportFormats(
    @SerialName("display_desc") val displayDesc: String,
    val format: String,
    @SerialName("new_description") val newDescription: String,
    val quality: Int,
    val superscript: String,
)

@Serializable
data class Dash(
    val audio: List<DashItem>?,
    val duration: Long,
    @SerialName("min_buffer_time") val minBufferTime: Double,
    val video: List<DashItem>,
)

@Serializable
data class DashItem(
    @SerialName("backup_url") val backupUrl: List<String>,
    val bandwidth: Int,
    @SerialName("base_url") val baseUrl: String,
    val codecid: Int,
    val codecs: String,
    @SerialName("frame_rate") val frameRate: String,
    val height: Int,
    val id: Int,
    @SerialName("mime_type") val mimeType: String,
    @SerialName("segment_base") val segmentBase: SegmentBase,
    val width: Int,
)

@Serializable
data class SegmentBase(
    @SerialName("index_range") val indexRange: String,
    val initialization: String,
)
