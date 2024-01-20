package com.storyteller_f.bi.player

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
): State<PlayerService> {
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
        log {
            "VideoPage() called disposable"
        }
        onDispose {
            log {
                "VideoPage() called dispose invoked"
            }
            reportProgress()
            player.stop()
            player.release()
        }
    })
    val mediaSourceGroup by produceState<MediaSourceGroup?>(
        initialValue = null,
        key1 = videoPlayerRepository
    ) {
        value = if (videoPlayerRepository != null)
            com.storyteller_f.bi.player.Player.mediaSource(videoPlayerRepository).getOrNull()
        else null
    }
    return remember {
        derivedStateOf {
            AndroidPlayerService(
                size,
                progress,
                reportProgress,
                videoPlayerRepository,
                mediaSourceGroup,
                player
            )
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
actual fun VideoView(
    service: PlayerService,
    aspectRatio: Boolean,
    startFullscreenMode: ((Boolean) -> Unit)?
) {
    service as AndroidPlayerService
    val player = service.player
    val context = LocalContext.current
    AndroidView(
        factory = {
            PlayerView(it)
        }, modifier = Modifier
            .fillMaxWidth()
            .let {
                if (aspectRatio) {
                    it.aspectRatio(16f / 9)
                } else it
            }
    ) {
        if (startFullscreenMode != null) {
            it.setFullscreenButtonClickListener { fullscreen ->
                startFullscreenMode(fullscreen)
            }
        }
        it.setShowSubtitleButton(true)
        it.player = service.player
        player.addMediaSource(service.source!!.mediaSource(context, service.repository!!))
        player.prepare()
        player.seekTo(service.initProgress)
        player.play()
        service.reportHistory()
    }
}