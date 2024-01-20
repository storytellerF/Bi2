package com.storyteller_f.bi.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchListInfo<T>(
    val items: List<T>?,
    val pages: Int,
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
    val archive: List<SearchVideoInfo>?,
)

@Serializable
data class SearchBangumiInfo(
    @SerialName("cat_desc") val catDesc: String,
    val cover: String,
    val finish: Int,
    val goto: String,
    val index: String,
    @SerialName("newest_cat") val newestCat: String,
    @SerialName("newest_season") val newestSeason: String,
    val param: String,
    val title: String,
    @SerialName("total_count") val totalCount: Int,
    val uri: String,
)

@Serializable
data class SearchResultInfo<T>(
    val attribute: Int,
    val items: T,
    val page: Int,
    val trackid: String,
)

@Serializable
data class SearchUpperInfo(
    val archives: Int,
    val cover: String,
    val fans: Int,
    val goto: String,
    val param: String,
    val sign: String,
    val status: Int,
    val title: String,
    @SerialName("total_count") val totalCount: Int,
    val uri: String,
)
