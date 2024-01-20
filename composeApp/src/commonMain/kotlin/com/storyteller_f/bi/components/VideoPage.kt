package com.storyteller_f.bi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moriatsushi.insetsx.rememberWindowInsetsController
import com.storyteller_f.bi.data.VideoId
import com.storyteller_f.bi.data.customViewModel
import com.storyteller_f.bi.entity.VideoInfo
import com.storyteller_f.bi.entity.VideoPageInfo
import com.storyteller_f.bi.entity.VideoTagInfo
import com.storyteller_f.bi.network.LoadingHandler
import com.storyteller_f.bi.network.Service.videoResultInfo
import com.storyteller_f.bi.network.error
import com.storyteller_f.bi.network.loaded
import com.storyteller_f.bi.network.loading
import com.storyteller_f.bi.player.PlayerSession
import com.storyteller_f.bi.player.VideoView
import com.storyteller_f.bi.player.rememberPlayerService
import com.storyteller_f.bi.repository.VideoPlayerRepository
import com.storyteller_f.bi.ui.RemoteImage
import com.storyteller_f.bi.ui.StandBy
import com.storyteller_f.bi.ui.StateView
import io.github.aakira.napier.log
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import net.sergeych.sprintf.sprintf

@Composable
fun VideoDescription(
    info: VideoInfo?,
    openComment: () -> Unit = {}
) {
    val pages = info?.pages
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(text = info?.title.orEmpty())
        val pubDate = info?.pubdate
        if (pubDate != null) {
            Instant.fromEpochSeconds(pubDate)
            Text(text = "%tO".sprintf(Instant.fromEpochSeconds(pubDate)))
        }
        if (!pages.isNullOrEmpty()) {
            PageRow(pages)
        }
        val tags = info?.tag.orEmpty()
        if (tags.isNotEmpty()) {
            TagRow(tags)
        }
        Button(onClick = { openComment() }) {
            Text(text = "open comments")
        }
    }
}

@Composable
private fun PageRow(pages: List<VideoPageInfo>) {
    LazyRow(modifier = Modifier.padding(top = 8.dp)) {
        items(pages.size) {
            val pageDetail = pages[it]
            Column(
                modifier = Modifier
                    .apply {
                        if (it != 0) padding(start = 8.dp)
                    }
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(8.dp)
            ) {
                Text(text = pageDetail.page.toString())
                Text(text = pageDetail.part, modifier = Modifier.widthIn(max = 200.dp))
            }
        }
    }
}

@Composable
private fun TagRow(tags: List<VideoTagInfo>) {
    LazyRow(modifier = Modifier.padding(top = 8.dp)) {
        items(tags, {
            it.tagId
        }) {
            Text(
                text = it.tagName,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(4.dp)

            )
        }
    }
}

class VideoViewModel(private val videoId: String) : ViewModel() {
    val handler = LoadingHandler<VideoInfo>(::load)
    val info = handler.data
    val state = handler.state

    val currentVideoRepository = info.map { info ->
        if (info == null) {
            null
        } else {
            VideoPlayerRepository(
                aid = info.aid.toString(),
                title = info.title,
                coverUrl = info.pic,
                id = info.cid.toString(),
            )
        }
    }

    init {
        load()
    }

    private fun load() {
        state.loading()
        viewModelScope.launch {
            val type = if (videoId.startsWith("BV")) {
                "BV"
            } else {
                "AV"
            }

            val res = videoResultInfo(type, videoId)

            res.fold(onSuccess = {
                val data = it.data!!
                info.value = data.copy(
                    desc = data.desc,
                    pages = data.pages.map { pageInfo ->
                        pageInfo.copy(
                            part = pageInfo.part.ifEmpty {
                                data.title
                            }
                        )
                    }
                )
                state.loaded()
            }, onFailure = {
                state.error(it)
            })
        }
    }
}

@Composable
fun VideoPage(
    playerSession: PlayerSession.VideoSession,
    requestOrientation: ((Boolean) -> Unit)? = null,
) {
    val videoId = playerSession.id
    val initProgress = playerSession.progress
    val videoViewModel = customViewModel(VideoViewModel::class, keys = listOf("video${playerSession.id}")) {
        set(VideoId, videoId)
    }

    /**
     * 一般来说就是全屏的意思
     */
    var videoOnly by remember {
        mutableStateOf(false)
    }
    val videoInfo by videoViewModel.info.collectAsState()
    val videoPlayerRepository by videoViewModel.currentVideoRepository.collectAsState(null)
    val playerKit = rememberPlayerService(
        videoPlayerRepository = videoPlayerRepository,
        initProgress = initProgress
    )
    val windowInsetsController = rememberWindowInsetsController()
    val size = playerKit.size

    // 全屏时依然保持竖屏状态
    val potentialPortrait = if (size != null) size.width < size.height else false
    val switchFullScreen = if (requestOrientation != null) {
        { fullScreenMode: Boolean ->
            videoOnly = fullScreenMode
            windowInsetsController?.setIsNavigationBarsVisible(false)
            windowInsetsController?.setIsStatusBarsVisible(false)
            if (!(potentialPortrait && fullScreenMode)) {
                requestOrientation.invoke(fullScreenMode)
            }
        }
    } else {
        null
    }
    StateView(videoViewModel.handler) {
        Column {
            if (!videoOnly) {
                Text(text = videoId)
            }
            VideoFrame(
                playerKit,
                !(potentialPortrait && videoOnly), // 竖屏全屏时不用保持16:9的画面比例
                switchFullScreen
            )
            if (!videoOnly) {
                VideoPageNavHost(videoInfo, videoId)
            }
        }
    }
}

/**
 * @param aspectRatio 是否保持播放器固定比例
 * @param switchFullscreenMode 为null，说明不支持全屏
 */
@Composable
fun VideoFrame(
    playerKit: PlayerService,
    aspectRatio: Boolean,
    switchFullscreenMode: ((Boolean) -> Unit)?
) {
    val cover = playerKit.repository?.coverUrl
    val mediaSource = playerKit.source
    if (mediaSource != null) {
        log {
            "VideoPage() called VideoView ${playerKit.initProgress}"
        }
        VideoView(
            playerKit,
            aspectRatio,
            switchFullscreenMode
        )
    } else {
        val coverModifier = Modifier.aspectRatio(16f / 9)
        StandBy(modifier = coverModifier) {
            RemoteImage(
                model = cover,
                contentDescription = "video cover",
                modifier = coverModifier
            )
        }
    }
}

@Composable
fun VideoPageNavHost(videoInfo: VideoInfo?, videoId: String) {
    val navigator = rememberNavController()
    NavHost(
        navController = navigator,
        startDestination = "/description",
    ) {
        composable(
            route = "/description",
        ) {
            VideoDescription(videoInfo) {
            }
        }

        composable(
            route = "/comment",
        ) {
            CommentsPage(videoId) {
            }
        }

        composable(
            route = "/comment/{id}",
        ) {
            val id = it.arguments?.getLong("id")!!
            CommentReplyPage(id, videoId.toLong())
        }
    }
}
