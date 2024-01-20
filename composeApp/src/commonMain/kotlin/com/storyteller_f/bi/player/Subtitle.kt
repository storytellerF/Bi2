package com.storyteller_f.bi.player

import com.storyteller_f.bi.entity.SubtitleJsonInfo
import com.storyteller_f.bi.fileSystem
import com.storyteller_f.bi.network.ktorClient
import com.storyteller_f.bi.repository.BasePlayerRepository
import com.storyteller_f.bi.repository.SubtitleSourceInfo
import com.storyteller_f.bi.userPath
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sergeych.sprintf.sprintf
import okio.Path.Companion.toPath

suspend fun BasePlayerRepository.subtitleCandidates(): List<SubtitleCandidate> {
    val subtitles = getSubtitles()

    val pairs = subtitles.map { info: SubtitleSourceInfo ->
        val path = userPath("/subtitle/$id/${info.lan}.srt")
        if (fileSystem.exists(path.toPath())) {
            path to info to null
        } else {
            val res = ktorClient.get(info.subtitleUrl).body<SubtitleJsonInfo>()
            writeToFile(res, path)
            path to info to res
        }
    }

    val configurations = pairs.map {
        it.first
    }.map { (path, info) ->
        SubtitleCandidate(path, info.lanDoc, info.id, info.lan)
    }
    val mixedSubtitle =
        createMixedSubtitleIfNeed(pairs, subtitles)
    return if (mixedSubtitle != null) {
        configurations + mixedSubtitle
    } else {
        configurations
    }
}

private suspend fun BasePlayerRepository.createMixedSubtitleIfNeed(
    pairs: List<Pair<Pair<String, SubtitleSourceInfo>, SubtitleJsonInfo?>>,
    subtitles: List<SubtitleSourceInfo>
): SubtitleCandidate? {
    val lanList = pairs.map { it.first.second.lan }
    val infoList = pairs.mapNotNull {
        it.second
    }
    val path = userPath("subtitle/$id/mix.srt")
    if (pairs.size == 2 &&
        infoList.size == 2 &&
        subtitles.size == 2 &&
        lanList.any {
            it.contains("zh")
        } && lanList.any {
            it.contains(
                "en"
            )
        }
    ) {
        val first = infoList.first()
        val jsonInfo = infoList.last()

        writeToFile(first, path) { itemInfo, i ->
            "${itemInfo.content}\n${jsonInfo.body[i].content}"
        }
        return SubtitleCandidate(path, "mix", 0, "mix")
    }
    return null
}

private suspend fun writeToFile(
    jInfo: SubtitleJsonInfo,
    file: String,
    c: (SubtitleJsonInfo.ItemInfo, Int) -> String = { itemInfo, _ -> itemInfo.content }
) {
    val content = jInfo.body.mapIndexed { index, itemInfo ->
        val toDuration = formatDuration(
            itemInfo.to
        )
        val fromDuration = formatDuration(itemInfo.from)
        """
            ${index + 1}
            $fromDuration --> $toDuration
            ${c(itemInfo, index)}
        """.trimIndent()
    }.joinToString("\n\n")
    withContext(Dispatchers.IO) {
        val path = file.toPath()
        val parent = path.parent!!
        if (!fileSystem.exists(parent)) {
            fileSystem.createDirectories(parent)
        }
        fileSystem.write(file.toPath()) {
            writeUtf8(content)
        }
    }
}

fun formatDuration(duration: Double): String {
    val toLong = duration.toLong()

    val seconds = toLong % 60
    val minutes = toLong / 60
    val hours = toLong / (60 * 60)
    val millis = ((duration - toLong) * 1000).toInt()

    return "%02d:%02d:%02d,%03d".sprintf(hours, minutes, seconds, millis)
}
