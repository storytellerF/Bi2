package com.storyteller_f.bi.repository

import com.storyteller_f.bi.network.DashSource
import com.storyteller_f.bi.network.Service.bangumiPlayUrlData
import com.storyteller_f.bi.network.Service.bangumiReport
import com.storyteller_f.bi.network.Service.bangumiResultInfo

class BangumiPlayerRepository(
    private val sid: String,
    private val epid: String,
    private val aid: String,
    override val id: String,
    override val title: String,
    override val coverUrl: String,
    override val ownerId: String,
    override val ownerName: String,
) : BasePlayerRepository() {

    override suspend fun getPlayerUrl(quality: Int, fnval: Int): Result<PlayerSourceInfo> {
        return bangumiPlayUrlData(quality, fnval, epid, id).map { res ->
            val dash = res.dash
            PlayerSourceInfo(
                res.lastPlayCid.orEmpty(),
                res.lastPlayTime ?: 0,
                res.quality,
                res.acceptQuality.mapIndexed { index, i ->
                    PlayerSourceInfo.AcceptInfo(i, res.acceptDescription[index])
                }, when {
                    dash != null -> {
                        val duration = dash.duration * 1000L
                        val dashSource = DashSource(res.quality, dash)
                        val dashVideo = dashSource.getDashVideo()!!
                        val height = dashVideo.height
                        val width = dashVideo.width
                        val url = dashSource.getMDPUrl(dashVideo)
                        VideoInfo2(duration, width, height, url)
                    }

                    else -> {
                        var duration = 0L
                        val url = "[concatenating]\n" + res.durl.orEmpty().joinToString("\n") { d ->
                            duration += d.length * 1000L
                            d.url
                        }
                        VideoInfo2(duration, 1, 1, url)
                    }
                })
        }
    }

    override suspend fun getSubtitles(): List<SubtitleSourceInfo> {
        return bangumiResultInfo(aid, id, epid, sid).fold(onSuccess = {
            it.data!!.subtitle.subtitles.map { subtitleX ->
                SubtitleSourceInfo(
                    id = subtitleX.id,
                    lan = subtitleX.lan,
                    lan_doc = subtitleX.lanDoc,
                    subtitle_url = subtitleX.subtitleUrl,
                    ai_status = subtitleX.aiStatus,
                )
            }

        }, onFailure = {
            it.printStackTrace()
            emptyList()
        })
    }

    override suspend fun historyReport(progress: Long) {
        try {
            val realtimeProgress = progress.toString()  // 秒数
            bangumiReport(realtimeProgress, aid, id, epid, sid)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}