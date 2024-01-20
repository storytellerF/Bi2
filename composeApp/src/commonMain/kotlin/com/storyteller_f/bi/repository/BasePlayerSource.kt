package com.storyteller_f.bi.repository

abstract class BasePlayerSource() {
    abstract val id: String // cid
    abstract val title: String
    abstract val coverUrl: String
    abstract val ownerId: String
    abstract val ownerName: String
    abstract suspend fun getPlayerUrl(quality: Int, fnval: Int): PlayerSourceInfo
    open suspend fun getSubtitles(): List<SubtitleSourceInfo> = emptyList()
    open suspend fun historyReport(progress: Long) {}

}