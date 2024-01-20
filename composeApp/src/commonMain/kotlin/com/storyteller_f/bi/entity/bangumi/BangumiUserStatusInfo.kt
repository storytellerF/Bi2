package com.storyteller_f.bi.entity.bangumi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
