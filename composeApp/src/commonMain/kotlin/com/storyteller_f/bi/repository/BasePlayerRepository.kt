package com.storyteller_f.bi.repository

class VideoInfo2(val duration: Long, val width: Int, val height: Int, val url: String)

abstract class BasePlayerRepository {
    abstract val id: String // cid
    abstract val title: String
    abstract val coverUrl: String
    abstract val ownerId: String
    abstract val ownerName: String
    abstract suspend fun getPlayerUrl(quality: Int, fnval: Int): Result<PlayerSourceInfo>
    open suspend fun getSubtitles(): List<SubtitleSourceInfo> = emptyList()
    open suspend fun historyReport(progress: Long) {}

}

fun getDefaultRequestProperties(playerSource: BasePlayerRepository): Map<String, String> {
    val header = HashMap<String, String>()
    if (playerSource is VideoPlayerRepository) {
        header["Referer"] = DEFAULT_REFERER
    }
    header["User-Agent"] = DEFAULT_USER_AGENT
    return header
}

private const val DEFAULT_REFERER = "https://www.bilibili.com/"
private const val DEFAULT_USER_AGENT = "Bilibili Freedoooooom/MarkII"