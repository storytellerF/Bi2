package com.storyteller_f.bi.repository

class VideoInfo2(val duration: Long, val width: Int, val height: Int, val url: String)

abstract class BasePlayerRepository {
    abstract val id: String // cid
    abstract val coverUrl: String
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

const val DEFAULT_REFERER = "https://www.bilibili.com/"
const val DEFAULT_USER_AGENT = "Bilibili Freedoooooom/MarkII"

data class SubtitleSourceInfo(
    val id: Long,
    val lan: String,
    val lanDoc: String,
    val subtitleUrl: String,
    val aiStatus: Int,
)

class PlayerSourceInfo(
    val quality: Int = 0,
    private val acceptList: List<AcceptInfo> = emptyList(),
    info2: VideoInfo2,
) {
    val url = info2.url
    val duration = info2.duration

    val description: String
        get() = acceptList.find { it.quality == quality }?.description ?: "未知清晰度"

    val height = info2.height
    val width = info2.width

    data class AcceptInfo(
        val quality: Int,
        val description: String,
    )
}
