package com.storyteller_f.bi.player

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.dash.manifest.DashManifestParser
import androidx.media3.exoplayer.source.*
import com.storyteller_f.bi.repository.BasePlayerRepository
import com.storyteller_f.bi.repository.getDefaultRequestProperties

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun MediaSourceGroup.mediaSource(context: Context, source: BasePlayerRepository): MediaSource {
    val subtitleMediaSources = subtitles.subtitleMediaSources(context)
    val header = getDefaultRequestProperties(source)
    val dataSourceFactory = DefaultHttpDataSource.Factory()
    dataSourceFactory.setDefaultRequestProperties(header)
    return when (this) {
        is MediaSourceGroup.Parts -> {
            // 视频拼接
            @Suppress("DEPRECATION")
            val mediaSource = ConcatenatingMediaSource().apply {
                addMediaSources(
                    url.map {
                        ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(it))
                    }
                )
            }
            if (subtitleMediaSources.isNotEmpty()) {
                MergingMediaSource(
                    mediaSource,
                    *subtitleMediaSources.toTypedArray()
                )
            } else {
                mediaSource
            }
        }

        is MediaSourceGroup.Dash -> {
            // Create a data source factory.
            // Create a DASH media source pointing to a DASH manifest uri.
            val dashManifest =
                DashManifestParser().parse(base.toUri(), data.toByteArray().inputStream())
            DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(dashManifest)
        }

        is MediaSourceGroup.VideoAndAudio -> {
            val videoMedia = MediaItem.fromUri(video)
            val audioMedia = MediaItem.fromUri(audio)
            if (local) {
                // 本地音视频分离
                val localSourceFactory = DefaultDataSource.Factory(context)
                MergingMediaSource(
                    ProgressiveMediaSource.Factory(localSourceFactory)
                        .createMediaSource(videoMedia),
                    ProgressiveMediaSource.Factory(localSourceFactory)
                        .createMediaSource(audioMedia)
                )
            } else {
                MergingMediaSource(
                    ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(videoMedia),
                    ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(audioMedia),
                    *subtitleMediaSources.toTypedArray()
                )
            }
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun List<SubtitleCandidate>.subtitleMediaSources(
    context: Context,
) = map {
    MediaItem.SubtitleConfiguration.Builder(it.path.toUri())
        .setMimeType(MimeTypes.APPLICATION_SUBRIP)
        .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
        .setLabel(it.label)
        .setId(it.id.toString())
        .setRoleFlags(C.ROLE_FLAG_SUBTITLE)
        .setLanguage(it.language)
        .build()
}.map {
    @Suppress("DEPRECATION")
    SingleSampleMediaSource.Factory(DefaultDataSource.Factory(context))
        .createMediaSource(it, C.TIME_UNSET)
}
