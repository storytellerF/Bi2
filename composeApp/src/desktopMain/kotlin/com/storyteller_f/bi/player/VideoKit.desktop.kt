package com.storyteller_f.bi.player

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.storyteller_f.bi.components.PlayerService
import com.storyteller_f.bi.components.VideoSize
import com.storyteller_f.bi.repository.BasePlayerRepository
import com.storyteller_f.bi.repository.DEFAULT_REFERER
import com.storyteller_f.bi.repository.DEFAULT_USER_AGENT
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.media.MediaParsedStatus
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.FlowLayout
import java.util.Locale
import javax.swing.JButton
import javax.swing.JPanel
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

    val mediaPlayerComponent = remember {
        initializeMediaPlayerComponent()
    }
    val mediaPlayer = mediaPlayerComponent.mediaPlayer()
    val events = mediaPlayer.events()
    events.addMediaEventListener(object : MediaEventAdapter() {
        override fun mediaParsedChanged(media: Media?, newStatus: MediaParsedStatus?) {
            super.mediaParsedChanged(media, newStatus)
            if (newStatus == MediaParsedStatus.DONE) {
                media?.info()?.videoTracks()?.firstOrNull()?.let {
                    size = VideoSize(it.width(), it.height())
                }
            }
        }
    })
    events.addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
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

    val mediaSourceGroup by produceState<MediaSourceGroup?>(
        initialValue = null,
        key1 = videoPlayerRepository
    ) {
        value = if (videoPlayerRepository != null) {
            val sourceGroup = Player.mediaSource(videoPlayerRepository).getOrNull()
            val mediaOptions = arrayOf(
                ":http-referrer=$DEFAULT_REFERER",
                ":http-user-agent=$DEFAULT_USER_AGENT",
            )
            val media = mediaPlayer.media()
            if (sourceGroup is MediaSourceGroup.VideoAndAudio) {
                val result = media.prepare(sourceGroup.video, *mediaOptions)
                Napier.i {
                    "prepare result $result"
                }
            } else if (sourceGroup is MediaSourceGroup.Parts) {
                val result = media.prepare(sourceGroup.url.first(), *mediaOptions)
                Napier.i {
                    "prepare result $result"
                }
            }
            sourceGroup
        } else {
            null
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
    return DesktopPlayerService(
        size,
        progress,
        reportProgress,
        videoPlayerRepository,
        mediaSourceGroup,
        mediaPlayerComponent
    )
}

class PlayerPanel(surfacePanel: Component) : JPanel() {

    private val mediaPlayer = surfacePanel.mediaPlayer()

    val pauseButton = JButton("▶️").apply {
        isFocusPainted = false
        isBorderPainted = false
        isContentAreaFilled = false
    }

    private val controlsPane = JPanel(FlowLayout(FlowLayout.CENTER)).apply {
        isOpaque = false
        background = Color(0, 0, 0, 0)
        add(pauseButton)
    }

    init {
        layout = BorderLayout()
        add(controlsPane, BorderLayout.SOUTH)
        add(surfacePanel, BorderLayout.CENTER)
        mediaPlayer.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun playing(mediaPlayer: MediaPlayer?) {
                super.playing(mediaPlayer)
                pauseButton.text = "⏸️"
            }

            override fun paused(mediaPlayer: MediaPlayer?) {
                super.paused(mediaPlayer)
                pauseButton.text = "▶️"
            }
        })
    }
}

@Composable
actual fun VideoView(
    service: PlayerService,
    aspectRatio: Boolean,
    switchFullscreenMode: ((Boolean) -> Unit)?
) {
    service as DesktopPlayerService
    val mediaPlayerComponent = service.component
    val mediaPlayer = mediaPlayerComponent.mediaPlayer()
    SwingPanel(
        factory = {
            PlayerPanel(mediaPlayerComponent)
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
        it.pauseButton.addActionListener {
            val status = mediaPlayer.status()
            Napier.i {
                "mediaPlayer eventListener performed ${status.isPlaying}"
            }
            val controls = mediaPlayer.controls()
            if (status.isPlaying) {
                controls.pause()
            } else {
                controls.play()
            }
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
