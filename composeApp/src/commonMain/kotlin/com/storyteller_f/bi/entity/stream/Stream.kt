package com.storyteller_f.bi.entity.stream

import com.storyteller_f.bi.entity.Response
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayurlData(
    @SerialName("accept_description") val acceptDescription: List<String>,
    @SerialName("accept_format") val acceptFormat: String,
    @SerialName("accept_quality") val acceptQuality: List<Int>,
    override val code: Int,
    val dash: Dash?,
    val durl: List<Durl>?,
    val format: String,
    val from: String,
    @SerialName("last_play_cid") val lastPlayCid: String?,
    @SerialName("last_play_time") val lastPlayTime: Long?,
    override val message: String,
    val quality: Int,
    val result: String,
    @SerialName("seek_param") val seekParam: String,
    @SerialName("seek_type") val seekType: String,
    @SerialName("support_formats") val supportFormats: List<SupportFormats>,
    val timelength: Int,
    @SerialName("video_codecid") val videoCodecid: Int,
) : Response<String> {
    override val res: String
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