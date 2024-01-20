package com.storyteller_f.bi.entity.bangumi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
