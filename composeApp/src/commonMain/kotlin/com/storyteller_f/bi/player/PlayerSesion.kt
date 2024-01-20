package com.storyteller_f.bi.player

sealed interface PlayerSession {
    class VideoSession(val id: String, val business: String, val progress: Long) : PlayerSession

    class BangumiSession(val id: String, val seasonId: String, val business: String, val progress: Long) :
        PlayerSession

}
