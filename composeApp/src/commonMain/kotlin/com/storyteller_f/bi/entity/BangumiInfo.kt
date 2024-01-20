package com.storyteller_f.bi.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BangumiInfo(
    val cover: String,
    val episodes: List<EpisodeInfo>,
    val evaluate: String,
    @SerialName("is_new_danmaku") val isNewDanmaku: Int,
    val link: String,
    @SerialName("media_id") val mediaId: Int,
    val mid: Int,
    val mode: Int,
    @SerialName("newest_ep") val newestEp: NewestEpisodeInfo,
    val rating: BangumiRatingInfo,
    val record: String,
    val rights: BangumiRightsInfo,
    @SerialName("season_id") val seasonId: String,
    @SerialName("season_status") val seasonStatus: Int,
    @SerialName("season_title") val seasonTitle: String,
    @SerialName("season_type") val seasonType: Int,
    val seasons: List<SeasonInfo>,
    @SerialName("series_id") val seriesId: Int,
    @SerialName("share_url") val shareUrl: String,
    @SerialName("square_cover") val squareCover: String,
    val stat: BangumiStatXInfo,
    val title: String,
    @SerialName("total_ep") val totalEp: Int,
    @SerialName("user_status") val userStatus: BangumiUserStatusInfo,
)

@Serializable
data class SeasonSectionInfo(
    @SerialName("main_section") val mainSection: SectionInfo?,
    val section: List<SectionInfo>,
) {
    @Serializable
    data class SectionInfo(
        val episodes: List<EpisodeInfo>,
        val id: String,
        val title: String,
        val type: Int,
    )
}

@Serializable
data class SeasonInfo(
    @SerialName("is_jump") val isJump: Int,
    @SerialName("is_new") val isNew: Int,
    @SerialName("season_id") val seasonId: String,
    @SerialName("season_title") val seasonTitle: String,
    val title: String,
)

@Serializable
data class NewestEpisodeInfo(
    val desc: String,
    val id: Int,
    val index: String,
    @SerialName("is_new") val isNew: Int,
    @SerialName("pub_real_time") val pubRealTime: String,
)

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

@Serializable
data class DimensionXInfo(
    val height: Int,
    val rotate: Int,
    val width: Int,
)

@Serializable
data class BangumiUserStatusInfo(
    @SerialName("is_vip") val isVip: Int,
    val pay: Int,
    @SerialName("pay_pack_paid") val payPackPaid: Int,
    val sponsor: Int,
    @SerialName("watch_progress") val watchProgress: WatchProgressInfo,
    var follow: Int,
) {
    @Serializable
    data class WatchProgressInfo(
        @SerialName("last_ep_id") val lastEpId: Long,
        @SerialName("last_ep_index") val lastEpIndex: String,
        @SerialName("last_time") val lastTime: Long,
    )
}

@Serializable
data class BangumiStatXInfo(
    val coins: String,
    val danmakus: String,
    val favorites: String,
    val reply: String,
    val share: String,
    val views: String,
)

@Serializable
data class BangumiRightsInfo(
    val autoplay: Int,
    val bp: Int,
    val download: Int,
    val elec: Int,
    val hd5: Int,
    @SerialName("is_cooperation") val isCooperation: Int,
    val movie: Int,
    @SerialName("no_reprint") val noReprint: Int,
    val pay: Int,
    @SerialName("ugc_pay") val ugcPay: Int,
)

@Serializable
data class BangumiRatingInfo(
    val count: Int,
    val score: Double,
)
