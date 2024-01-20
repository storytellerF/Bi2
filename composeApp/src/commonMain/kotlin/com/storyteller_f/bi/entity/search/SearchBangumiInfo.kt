package com.storyteller_f.bi.entity.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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