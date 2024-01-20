package com.storyteller_f.bi.entity.bangumi

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