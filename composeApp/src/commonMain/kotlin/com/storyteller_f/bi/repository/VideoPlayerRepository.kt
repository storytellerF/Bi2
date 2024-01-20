package com.storyteller_f.bi.repository

import com.storyteller_f.bi.network.DashSource
import com.storyteller_f.bi.network.Service.videoPlayerResultInfo
import com.storyteller_f.bi.network.Service.videoPlayurlData
import com.storyteller_f.bi.network.Service.videoReport
import io.github.aakira.napier.Napier

class VideoPlayerRepository(
    val aid: String, // av号
    val title: String,
    override val coverUrl: String,
    override var id: String, // cid
) : BasePlayerRepository() {

    override suspend fun getPlayerUrl(quality: Int, fnval: Int): Result<PlayerSourceInfo> {
        return videoPlayurlData(quality, fnval, aid, id).map {
            val res = it.data!!
            val dash = res.dash
            val durl = res.durl.orEmpty()

            PlayerSourceInfo(
                res.quality,
                res.acceptQuality.mapIndexed { index, i ->
                    PlayerSourceInfo.AcceptInfo(i, res.acceptDescription[index])
                },
                if (dash != null) {
                    val duration = dash.duration * 1000L
                    val dashSource = DashSource(res.quality, dash)
                    val dashVideo = dashSource.getDashVideo()!!
                    val height = dashVideo.height
                    val width = dashVideo.width
                    val url = dashSource.getMDPUrl(dashVideo)
                    VideoInfo2(duration, width, height, url)
                } else {
                    val duration = durl.sumOf {
                        it.length * 1000L
                    }
                    val url = "[concatenating]\n" + durl.joinToString("\n") { d ->
                        d.url
                    }
                    VideoInfo2(duration, 1, 1, url)
                }
            )
        }
    }

    override suspend fun getSubtitles(): List<SubtitleSourceInfo> {
        return videoPlayerResultInfo(aid, id).fold(onSuccess = { res ->
            res.data!!.subtitle.subtitles.map {
                SubtitleSourceInfo(
                    id = it.id,
                    lan = it.lan,
                    lanDoc = it.lanDoc,
                    subtitleUrl = it.subtitleUrl,
                    aiStatus = it.aiStatus,
                )
            }
        }, onFailure = {
            emptyList()
        })
    }

    override suspend fun historyReport(progress: Long) {
        try {
            val realtimeProgress = progress.toString() // 秒数
            videoReport(realtimeProgress, aid, id)
        } catch (e: Exception) {
            Napier.e(e) {
                "report progress failed"
            }
        }
    }
}
