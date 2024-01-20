package com.storyteller_f.bi.entity.video

import kotlinx.serialization.Serializable


@Serializable
data class VideoStaffInfo(
    var mid: String,
    var title: String,
    var face: String,
    var name: String
)