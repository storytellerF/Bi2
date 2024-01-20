package com.storyteller_f.bi.entity.bangumi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeInfo(
    val aid: String,
    val attr: Int,
    @SerialName("badge_info") val badgeInfo: EpisodeBadgeInfo? = null,
    @SerialName("badge") val badgeText: String,
    @SerialName("badge_type") val badgeType: Int,
    val bvid: String,
    val cid: String,
    val cover: String,
    val ctime: String,
    val dimension: DimensionXInfo? = null,
    val duration: Long,
    @SerialName("ep_id") val epId: String,
    @SerialName("episode_status") val episodeStatus: Int,
    @SerialName("episode_type") val episodeType: Int,
    val from: String,
    /**
     * 就是ep id
     */
    val id: String? = null,
    val index: String,
    @SerialName("index_title") val indexTitle: String?,
    @SerialName("long_title") val longTitle: String? = null,
    val mid: Int,
    val page: Int,
    val premiere: Boolean,
    @SerialName("pub_real_time") val pubRealTime: String,
    @SerialName("section_id") val sectionId: Int,
    @SerialName("section_type") val sectionType: Int,
    @SerialName("share_url") val shareUrl: String,
    val status: Int? = null,
    val title: String? = null,
    val vid: String,
) {


    val safeTitle
        get() = longTitle?.takeIf { it.isNotEmpty() } ?: title ?: indexTitle ?: "-"

    @Serializable
    data class EpisodeBadgeInfo(
        @SerialName("bg_color") val bgColor: String,
        @SerialName("bg_color_night") val bgColorNight: String,
        val text: String,
    )

}
