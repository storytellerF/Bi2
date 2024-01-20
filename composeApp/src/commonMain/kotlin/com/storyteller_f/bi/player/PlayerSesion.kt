package com.storyteller_f.bi.player

import kotlinx.serialization.Serializable

@Serializable
sealed interface PlayerSession {
    @Serializable
    data class VideoSession(val id: String, val business: String, val progress: Long) : PlayerSession

    @Serializable
    data class BangumiSession(val id: String, val seasonId: String, val business: String, val progress: Long) :
        PlayerSession
}
