package com.storyteller_f.bi.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpaceInfo(
    val card: CardInfo,
    val live: LiveInfo,
    val images: ImagesInfo,
    val favourite: Media<FavouriteItem>,
    val favourite2: Media<Favourite2Item>,
    val season: Media<SeasonItem>,
    val archive: Media<ArchiveItem>,
    @SerialName("coin_archive") val coinArchive: Media<ArchiveItem>,
    @SerialName("like_archive") val likeArchive: Media<ArchiveItem>,
    val tab: Tab
) {
    @Serializable
    data class CardInfo(
        val approve: Boolean,
        val article: Int,
        val attention: Int,
        val birthday: String,
        val description: String,
        val face: String,
        val fans: Int,
        val friend: Int,
        @SerialName("level_info") val levelInfo: LevelInfo,
        val mid: String,
        val name: String,
        val place: String,
        val rank: String,
        val regtime: Int,
        val relation: RelationInfo,
        val sex: String,
        val sign: String,
        val spacesta: Int
    )

    @Serializable
    data class RelationInfo(
        val status: Int,
        @SerialName("is_follow") val isFollow: Int,
    )

    @Serializable
    data class LevelInfo(
        @SerialName("current_exp") val currentExp: Int,
        @SerialName("current_level") val currentLevel: Int,
        @SerialName("current_min") val currentMin: Int,
        @SerialName("next_exp") val nextExp: String
    )

    @Serializable
    data class ImagesInfo(
        val imgUrl: String
    )

    @Serializable
    data class LiveInfo(
        val url: String,
        val title: String,
        val cover: String,
        val roomid: Long
    )

    @Serializable
    data class Tab(
        val archive: Boolean,
        val favorite: Boolean,
        val bangumi: Boolean,
        val like: Boolean
    )

    @Serializable
    data class Media<T>(
        val count: Int,
        val item: List<T>
    )

    @Serializable
    data class FavouriteItem(
        @SerialName("atten_count") val attenCount: Int,
        val cover: List<FavouriteItemCover>,
        val ctime: Int,
        @SerialName("cur_count") val curCount: Int,
        val fid: Long,
        @SerialName("max_count") val maxCount: Int,
        @SerialName("media_id") val mediaId: Long,
        val mid: Long,
        val mtime: Long,
        val name: String,
        val state: Int
    )

    @Serializable
    data class Favourite2Item(
        @SerialName("media_id") val mediaId: String,
        val id: String,
        val mid: String,
        val title: String,
        val cover: String,
        val count: Int,
        val type: Int,
        @SerialName("is_public") val isPublic: Int,
        val ctime: String,
        val mtime: String,
        @SerialName("is_default") val isDefault: Boolean,
    )

    @Serializable
    data class FavouriteItemCover(
        val aid: Int,
        val pic: String,
        val type: Int
    )

    @Serializable
    data class ArchiveItem(
        val cover: String,
        val ctime: Int,
        val danmaku: Int,
        val duration: Int,
        val goto: String,
        val length: String,
        val `param`: String,
        val play: Int,
        val state: Boolean,
        val title: String,
        val tname: String,
        @SerialName("ugc_pay") val ugcPay: Int,
        val uri: String
    )

    @Serializable
    data class SeasonItem(
        val attention: String,
        val cover: String,
        val finish: Int,
        val goto: String,
        val index: String,
        @SerialName("is_finish") val isFinish: String,
        @SerialName("is_started") val isStarted: Int,
        val mtime: Int,
        @SerialName("newest_ep_id") val newestEpId: String,
        @SerialName("newest_ep_index") val newestEpIndex: String,
        val `param`: String,
        val title: String,
        @SerialName("total_count") val totalCount: String,
        val uri: String
    )
}

@Serializable
data class UserInfo(
    val mid: Long,
    val name: String,
    val face: String,
    val sign: String? = null,
    val coin: Double,
    val bcoin: Double,
    val sex: Int,
    val rank: Int,
    val silence: Int,
    @SerialName("show_videoup") val showVideoup: Int,
    @SerialName("show_creative") val showCreative: Int,
    val level: Int,
    @SerialName("vip_type") val vipType: Int,
    @SerialName("audio_type") val audioType: Int,
    val dynamic: Int,
    val following: Int,
    val follower: Int
)
