package com.storyteller_f.bi.player

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.storyteller_f.bi.components.PlayerService
import com.storyteller_f.bi.components.VideoSize
import com.storyteller_f.bi.repository.BasePlayerRepository
import io.github.aakira.napier.log
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.launch
import platform.AVFoundation.*
import platform.AVKit.AVPlayerViewController
import platform.CoreGraphics.CGRect
import platform.Foundation.NSURL
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIView
import kotlin.time.Duration.Companion.seconds

class IosPlayerService(
    size: VideoSize?,
    initProgress: Long,
    reportHistory: () -> Unit,
    repository: BasePlayerRepository?,
    source: MediaSourceGroup?,
    val player: AVPlayer?,
) : PlayerService(
    size,
    initProgress,
    reportHistory,
    repository,
    source
)

@OptIn(ExperimentalForeignApi::class)
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

    val mediaSourceGroup by produceState<MediaSourceGroup?>(
        initialValue = null,
        key1 = videoPlayerRepository
    ) {
        value = if (videoPlayerRepository != null) {
            Player.mediaSource(videoPlayerRepository).getOrNull()
        } else {
            null
        }
    }
    val avAsset by derivedStateOf {
        when (val m = mediaSourceGroup) {
            is MediaSourceGroup.VideoAndAudio -> AVAsset.assetWithURL(NSURL.URLWithString(m.video)!!)
            else -> null
        }
    }
    val player = remember {
        val asset = avAsset
        if (asset != null) {
            AVPlayer.playerWithPlayerItem(AVPlayerItem(asset))
        } else {
            null
        }
    }
    LaunchedEffect(avAsset) {
        avAsset?.tracksWithMediaType(AVMediaTypeVideo)?.filterIsInstance<AVAssetTrack>()?.firstOrNull()?.let {
            val size1 = it.naturalSize
            val transform = it.preferredTransform
            val a = transform.useContents {
                a.toInt()
            }
            val b = transform.useContents {
                b.toInt()
            }
            val c = transform.useContents {
                c.toInt()
            }
            val d = transform.useContents {
                d.toInt()
            }
            size = if (a == 0 && b == 1 && c == -1 && d == 0 ||
                a == 0 && b == -1 && c == 1 && d == 0
            ) {
                size1.useContents {
                    VideoSize(height.toInt(), width.toInt())
                }
            } else {
                size1.useContents {
                    VideoSize(width.toInt(), height.toInt())
                }
            }
        }
    }
    val reportProgress: () -> Unit = {
        val p = player?.currentItem?.run {
            currentTime().useContents {
                value
            }
        }
        if (p != null) {
            log {
                "report progress $p"
            }
            scope.launch {
                videoPlayerRepository?.historyReport(p)
            }
        }
    }
    return remember {
        derivedStateOf {
            IosPlayerService(
                size,
                progress,
                reportProgress,
                videoPlayerRepository,
                mediaSourceGroup,
                player,
            )
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoView(
    service: PlayerService,
    aspectRatio: Boolean,
    startFullscreenMode: ((Boolean) -> Unit)?
) {
    service as IosPlayerService
    if (service.player != null) {
        val avPlayerViewController = AVPlayerViewController()
        avPlayerViewController.player = service.player
        UIKitView(
            factory = {
                // Create a UIView to hold the AVPlayerLayer
                val playerContainer = UIView()
                playerContainer.addSubview(avPlayerViewController.view)
                // Return the playerContainer as the root UIView
                playerContainer
            },
            onResize = { view: UIView, rect: CValue<CGRect> ->
                CATransaction.begin()
                CATransaction.setValue(true, kCATransactionDisableActions)
                view.layer.setFrame(rect)
                avPlayerViewController.view.layer.frame = rect
                CATransaction.commit()
            },
            update = {
                service.player.play()
            },
            modifier = Modifier
        )
    }
}
