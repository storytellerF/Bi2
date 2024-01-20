package com.storyteller_f.bi.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.storyteller_f.bi.components.PlayerService
import com.storyteller_f.bi.components.VideoSize
import com.storyteller_f.bi.repository.BasePlayerRepository
import com.storyteller_f.bi.repository.DEFAULT_REFERER
import com.storyteller_f.bi.repository.DEFAULT_USER_AGENT
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.media.MediaParsedStatus
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
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
): PlayerService {
    Napier.i {
        "desktop player service"
    }
    val scope = rememberCoroutineScope()

    var size by remember {
        mutableStateOf<VideoSize?>(null)
    }
    val progress by remember {
        mutableLongStateOf(initProgress.coerceAtLeast(0L).seconds.inWholeMilliseconds)
    }

    val mediaPlayerComponent = initializeMediaPlayerComponent()
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
    mediaPlayer.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
        override fun error(mediaPlayer: MediaPlayer?) {
            super.error(mediaPlayer)
            Napier.i {
                "vlc error"
            }
        }

        override fun buffering(mediaPlayer: MediaPlayer?, newCache: Float) {
            super.buffering(mediaPlayer, newCache)
            Napier.i {
                "vlc buffering $newCache"
            }
        }

        override fun playing(mediaPlayer: MediaPlayer?) {
            super.playing(mediaPlayer)
            Napier.i {
                "vlc playing"
            }
        }

        override fun mediaPlayerReady(mediaPlayer: MediaPlayer?) {
            super.mediaPlayerReady(mediaPlayer)
            Napier.i {
                "vlc player ready"
            }
        }

        override fun positionChanged(mediaPlayer: MediaPlayer?, newPosition: Float) {
            super.positionChanged(mediaPlayer, newPosition)
            Napier.i {
                "vlc player position change $newPosition"
            }
        }

        override fun volumeChanged(mediaPlayer: MediaPlayer?, volume: Float) {
            super.volumeChanged(mediaPlayer, volume)
            Napier.i {
                "vlc player volume changed $volume"
            }
        }
    })
    val reportProgress: () -> Unit = {
        scope.launch {
        }
    }

    DisposableEffect(mediaPlayerComponent) {
        onDispose {
            Napier.i {
                "desktop video dispose"
            }
            try {
                mediaPlayerComponent.release()
            } catch (e: Exception) {
                Napier.e(e) {
                    "release vlc failed"
                }
            }
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
    return DesktopPlayerService(
        size,
        progress,
        reportProgress,
        videoPlayerRepository,
        mediaSourceGroup,
        mediaPlayerComponent
    )
}

@Composable
actual fun VideoView(
    service: PlayerService,
    aspectRatio: Boolean,
    switchFullscreenMode: ((Boolean) -> Unit)?
) {
    service as DesktopPlayerService
    val mediaPlayer = service.component.mediaPlayer()
    Column {
        SwingPanel(
            factory = {
                service.component
            },
            modifier = Modifier.fillMaxWidth()
                .let {
                    if (aspectRatio) {
                        it.aspectRatio(16f / 9)
                    } else {
                        it
                    }
                }
        ) {
        }
        IconButton({
            val source = service.source
            val mediaOptions = arrayOf(
                ":http-referrer=$DEFAULT_REFERER",
                ":http-user-agent=$DEFAULT_USER_AGENT",
            )
            if (source is MediaSourceGroup.VideoAndAudio) {
                mediaPlayer.media().play(source.video, *mediaOptions)
            } else if (source is MediaSourceGroup.Parts) {
                mediaPlayer.media().play(source.url.first(), *mediaOptions)
            }
        }) {
            Icon(Icons.Default.PlayCircle, "play")
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
