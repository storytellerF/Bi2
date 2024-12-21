package com.storyteller_f.bi.player

import androidx.compose.runtime.*
import androidx.compose.ui.awt.SwingPanel
import com.storyteller_f.bi.components.PlayerService
import com.storyteller_f.bi.components.VideoSize
import com.storyteller_f.bi.repository.BasePlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.media.MediaParsedStatus
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import java.awt.Component
import java.util.*
import kotlin.time.Duration.Companion.seconds

class DesktopPlayerService(
    size: VideoSize?,
    initProgress: Long,
    reportHistory: () -> Unit,
    repository: BasePlayerRepository?,
    source: MediaSourceGroup?,
    val component: Component,
) : PlayerService(
    size,
    initProgress,
    reportHistory,
    repository,
    source
)

@Composable
actual fun rememberPlayerService(
    videoPlayerRepository: BasePlayerRepository?,
    initProgress: Long
): State<PlayerService> {
    val scope = rememberCoroutineScope()

    var size by remember {
        mutableStateOf<VideoSize?>(null)
    }
    val progress by remember {
        mutableLongStateOf(initProgress.coerceAtLeast(0L).seconds.inWholeMilliseconds)
    }

    val mediaPlayerComponent = remember { initializeMediaPlayerComponent() }
    val mediaPlayer = mediaPlayerComponent.mediaPlayer()
    mediaPlayer.events().addMediaEventListener(object : MediaEventAdapter() {
        override fun mediaParsedChanged(media: Media?, newStatus: MediaParsedStatus?) {
            super.mediaParsedChanged(media, newStatus)
            if (newStatus == MediaParsedStatus.DONE) {
                media?.info()?.videoTracks()?.firstOrNull()?.let {
                    size = VideoSize(it.width(), it.height())
                }
            }
        }
    })
    val reportProgress: () -> Unit = {
        scope.launch {
        }
    }

    DisposableEffect(mediaPlayerComponent) {
        onDispose {
            mediaPlayerComponent.release()
        }
    }

    val mediaSourceGroup by produceState<MediaSourceGroup?>(
        initialValue = null,
        key1 = videoPlayerRepository
    ) {
        value = if (videoPlayerRepository != null) {
            withContext(Dispatchers.IO) {
                Player.mediaSource(videoPlayerRepository).getOrNull()
            }
        } else {
            null
        }
    }
    LaunchedEffect(mediaPlayer, mediaSourceGroup) {
        val m = mediaSourceGroup
        if (m is MediaSourceGroup.VideoAndAudio) {
            val media = mediaPlayer.media()
            media.prepare(m.video)
            media.parsing().parse()
        }
    }
    return remember {
        derivedStateOf {
            DesktopPlayerService(
                size,
                progress,
                reportProgress,
                videoPlayerRepository,
                mediaSourceGroup,
                mediaPlayerComponent
            )
        }
    }
}

@Composable
actual fun VideoView(
    service: PlayerService,
    aspectRatio: Boolean,
    switchFullscreenMode: ((Boolean) -> Unit)?
) {
    service as DesktopPlayerService
    val mediaPlayer = service.component.mediaPlayer()
    SwingPanel(factory = {
        service.component
    }) {
        val source = service.source
        if (source is MediaSourceGroup.VideoAndAudio) {
            mediaPlayer.media().play(source.video)
        }
    }
    DisposableEffect(service) {
        onDispose {
            mediaPlayer.release()
        }
    }
}

fun initializeMediaPlayerComponent(): Component {
    NativeDiscovery().discover()
    return if (isMacOS()) {
        CallbackMediaPlayerComponent()
    } else {
        EmbeddedMediaPlayerComponent()
    }
}

/**
 * Returns [MediaPlayer] from player components.
 * The method names are the same, but they don't share the same parent/interface.
 * That's why we need this method.
 */
fun Component.mediaPlayer(): EmbeddedMediaPlayer = when (this) {
    is CallbackMediaPlayerComponent -> mediaPlayer()
    is EmbeddedMediaPlayerComponent -> mediaPlayer()
    else -> error("mediaPlayer() can only be called on vlcj player components")
}

fun Component.release() {
    when (this) {
        is CallbackMediaPlayerComponent -> release()
        is EmbeddedMediaPlayerComponent -> release()
        else -> error("mediaPlayer() can only be called on vlcj player components")
    }
}

private fun isMacOS(): Boolean {
    val os = System
        .getProperty("os.name", "generic")
        .lowercase(Locale.ENGLISH)
    return "mac" in os || "darwin" in os
}
