package com.storyteller_f.bi.entity.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    var mid: Long,
    var name: String,
    var face: String,
    val sign: String?,
    var coin: Double,
    var bcoin: Double,
    var sex: Int,
    var rank: Int,
    var silence: Int,
    @SerialName("show_videoup") val showVideoup: Int,
    @SerialName("show_creative") val showCreative: Int,
    var level: Int,
    @SerialName("vip_type") val vipType: Int,
    @SerialName("audio_type") val audioType: Int,
    var dynamic: Int,
    var following: Int,
    var follower: Int
)