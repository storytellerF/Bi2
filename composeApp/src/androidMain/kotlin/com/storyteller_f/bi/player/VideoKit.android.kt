package com.storyteller_f.bi.player

import android.content.Context
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.storyteller_f.bi.components.PlayerService
import com.storyteller_f.bi.components.VideoSize
import com.storyteller_f.bi.repository.BasePlayerRepository
import io.github.aakira.napier.log
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class AndroidPlayerService(
    size: VideoSize?,
    progress: Long,
    reportHistory: () -> Unit,
    repository: BasePlayerRepository?,
    source: MediaSourceGroup?,
    val player: ExoPlayer,
) : PlayerService(size, progress, reportHistory, repository, source)

@Composable
actual fun rememberPlayerService(
    videoPlayerRepository: BasePlayerRepository?,
    initProgress: Long
): PlayerService {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var size by remember {
        mutableStateOf<VideoSize?>(null)
    }
    val progress by remember {
        mutableLongStateOf(initProgress.coerceAtLeast(0L).seconds.inWholeMilliseconds)
    }

    val player = remember {
        log {
            "VideoPage() called create player"
        }
        ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onVideoSizeChanged(videoSize: androidx.media3.common.VideoSize) {
                    super.onVideoSizeChanged(videoSize)
                    size = VideoSize(videoSize.width, videoSize.height)
                }
            })
        }
    }

    val reportProgress: () -> Unit = {
        scope.launch {
            videoPlayerRepository?.historyReport(player.currentPosition)
        }
    }
    DisposableEffect(key1 = player, effect = {
        releaseResource(reportProgress, player)
    })
    val mediaSourceGroup by produceState<MediaSourceGroup?>(
        initialValue = null,
        key1 = videoPlayerRepository
    ) {
        value = if (videoPlayerRepository != null) {
            com.storyteller_f.bi.player.Player.mediaSource(videoPlayerRepository).getOrNull()
        } else {
            null
        }
    }
    LaunchedEffect(mediaSourceGroup, videoPlayerRepository) {
        prepareResource(mediaSourceGroup, videoPlayerRepository, player, context)
    }
    return AndroidPlayerService(size, progress, reportProgress, videoPlayerRepository, mediaSourceGroup, player)
}

private fun DisposableEffectScope.releaseResource(
    reportProgress: () -> Unit,
    player: ExoPlayer
): DisposableEffectResult {
    log {
        "VideoPage() called disposable"
    }
    return onDispose {
        log {
            "VideoPage() called dispose invoked"
        }
        reportProgress()
        player.stop()
        player.release()
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
private fun prepareResource(
    mediaSourceGroup: MediaSourceGroup?,
    videoPlayerRepository: BasePlayerRepository?,
    player: ExoPlayer,
    context: Context
) {
    if (mediaSourceGroup != null && videoPlayerRepository != null) {
        player.clearMediaItems()
        player.addMediaSource(mediaSourceGroup.mediaSource(context, videoPlayerRepository))
        player.prepare()
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
actual fun VideoView(
    service: PlayerService,
    aspectRatio: Boolean,
    switchFullscreenMode: ((Boolean) -> Unit)?
) {
    service as AndroidPlayerService
    val player = service.player
    AndroidView(
        factory = {
            PlayerView(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (aspectRatio) {
                    it.aspectRatio(16f / 9)
                } else {
                    it
                }
            }
    ) {
        if (switchFullscreenMode != null) {
            it.setFullscreenButtonClickListener { fullscreen ->
                switchFullscreenMode(fullscreen)
            }
        }
        it.setShowSubtitleButton(true)
        it.player = service.player
        player.seekTo(service.initProgress)
        service.reportHistory()
    }
}
