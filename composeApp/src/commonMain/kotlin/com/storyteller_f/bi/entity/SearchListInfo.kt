package com.storyteller_f.bi.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchListInfo<T>(
    var items: List<T>?,
    var pages: Int,
)

@Serializable
data class SearchVideoInfo(
    val author: String,
    val cover: String,
    val danmaku: String,
    val desc: String,
    val duration: String,
    val goto: String,
    val mid: String,
    val `param`: String,
    val play: String,
    val status: Int,
    val title: String,
    @SerialName("total_count") val totalCount: Long,
    val uri: String,
)

@Serializable
data class SearchArchiveInfo(
    var archive: List<SearchVideoInfo>?,
)

@Serializable
data class SearchBangumiInfo(
    @SerialName("cat_desc") val catDesc: String,
    var cover: String,
    var finish: Int,
    var goto: String,
    var index: String,
    @SerialName("newest_cat") val newestCat: String,
    @SerialName("newest_season") val newestSeason: String,
    var param: String,
    var title: String,
    @SerialName("total_count") val totalCount: Int,
    var uri: String,
)

@Serializable
data class SearchResultInfo<T>(
    var attribute: Int,
    var items: T,
    var page: Int,
    var trackid: String,
)

@Serializable
data class SearchUpperInfo(
    var archives: Int,
    var cover: String,
    var fans: Int,
    var goto: String,
    var param: String,
    var sign: String,
    var status: Int,
    var title: String,
    @SerialName("total_count") val totalCount: Int,
    var uri: String,
)
