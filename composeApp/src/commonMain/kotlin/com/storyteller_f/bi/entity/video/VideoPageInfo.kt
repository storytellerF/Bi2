package com.storyteller_f.bi.entity.video

import kotlinx.serialization.Serializable


@Serializable
data class VideoPageInfo(
    val cid: String,


    val dmlink: String,
    val duration: Int,
    val from: String,

    val page: Int,
    var part: String,
    val vid: String,
    val weblink: String
)