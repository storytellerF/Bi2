package com.storyteller_f.bi.player

import com.storyteller_f.bi.repository.BasePlayerRepository
import io.github.aakira.napier.Napier

class SubtitleCandidate(val path: String, val label: String, val id: Long, val language: String)

sealed class MediaSourceGroup(val subtitles: List<SubtitleCandidate>) {
    class VideoAndAudio(val video: String, val audio: String, val local: Boolean, subtitles: List<SubtitleCandidate>) :
        MediaSourceGroup(subtitles)

    class Parts(val url: List<String>, subtitles: List<SubtitleCandidate>) : MediaSourceGroup(subtitles)

    class Dash(val base: String, val data: String, subtitles: List<SubtitleCandidate>) : MediaSourceGroup(subtitles)
}

object Player {
    suspend fun mediaSource(source: BasePlayerRepository): Result<MediaSourceGroup> {
        return source.getPlayerUrl(64, 1).map { playerUrl ->
            val url = playerUrl.url
            val subtitleCandidates = kotlin.runCatching {
                source.subtitleCandidates()
            }.fold({
                it
            }) {
                Napier.e(throwable = it) {
                    "save subtitle failed"
                }
                emptyList()
            }
            val part = url.split("\n")
            when (val type = part.first()) {
                "[local-merging]" -> MediaSourceGroup.VideoAndAudio(part[1], part[2], true, subtitleCandidates)
                "[merging]" -> MediaSourceGroup.VideoAndAudio(part[1], part[2], false, subtitleCandidates)
                "[concatenating]" -> MediaSourceGroup.Parts(part.subList(1, part.size), subtitleCandidates)
                "[dash-mpd]" -> MediaSourceGroup.Dash(part[1], part[2], subtitleCandidates)
                else -> throw Exception("not support $type")
            }
        }
    }
}
