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
import com.moriatsushi.insetsx.rememberWindowInsetsController
import com.storyteller_f.bi.data.VideoId
import com.storyteller_f.bi.data.viewModel
import com.storyteller_f.bi.entity.video.VideoInfo
import com.storyteller_f.bi.entity.video.VideoPageInfo
import com.storyteller_f.bi.entity.video.VideoTagInfo
import com.storyteller_f.bi.network.LoadingHandler
import com.storyteller_f.bi.network.Service.videoResultInfo
import com.storyteller_f.bi.network.error
import com.storyteller_f.bi.network.loaded
import com.storyteller_f.bi.network.loading
import com.storyteller_f.bi.player.VideoView
import com.storyteller_f.bi.player.rememberPlayerService
import com.storyteller_f.bi.repository.VideoPlayerRepository
import com.storyteller_f.bi.ui.RemoteImage
import com.storyteller_f.bi.ui.StandBy
import com.storyteller_f.bi.ui.StateView
import com.storyteller_f.bi.player.PlayerSession
import io.github.aakira.napier.log
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.viewmodel.viewModelScope
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
            Column(modifier = Modifier
                .apply {
                    if (it != 0) padding(start = 8.dp)
                }
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(8.dp)) {
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


class VideoViewModel(private val videoId: String) : com.storyteller_f.bi.data.ViewModel() {
    val handler = LoadingHandler<VideoInfo>(::load)
    val info = handler.data
    val state = handler.state

    val currentVideoRepository = info.map { info ->
        if (info == null) null
        else
            VideoPlayerRepository(
                aid = info.aid,
                title = info.title,
                coverUrl = info.pic,
                id = info.cid.toString(),
                ownerId = info.owner.mid,
                ownerName = info.owner.name,
            )
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
                        pageInfo.copy(part = pageInfo.part.ifEmpty {
                            data.title
                        })
                    })
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
    val videoViewModel =
        viewModel(VideoViewModel::class) {
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
    val playerKit by rememberPlayerService(
        videoPlayerRepository = videoPlayerRepository,
        initProgress = initProgress
    )
    val windowInsetsController = rememberWindowInsetsController()
    val size = playerKit.size

    //全屏时依然保持竖屏状态
    val potentialPortrait = if (size != null) size.width < size.height else false
    val startFullscreenMode = if (requestOrientation != null) { it: Boolean ->
        videoOnly = it
        windowInsetsController?.setIsNavigationBarsVisible(false)
        windowInsetsController?.setIsStatusBarsVisible(false)
        if (!(potentialPortrait && it)) {
            requestOrientation.invoke(it)
        }
    } else null
    StateView(videoViewModel.handler) {
        Column {
            if (!videoOnly)
                Text(text = videoId)
            VideoFrame(
                playerKit,
                !(potentialPortrait && videoOnly),//竖屏全屏时不用保持16:9的画面比例
                startFullscreenMode
            )
            if (!videoOnly) {
                VideoPageNavHost(videoInfo, videoId)
            }

        }
    }
}

/**
 * @param aspectRatio 是否保持播放器固定比例
 * @param startFullscreenMode 为null，说明不支持全屏
 */
@Composable
fun VideoFrame(
    playerKit: PlayerService,
    aspectRatio: Boolean,
    startFullscreenMode: ((Boolean) -> Unit)?
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
            startFullscreenMode
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
    // Define a navigator, which is a replacement for Jetpack Navigation's NavController
    val navigator = rememberNavigator()
    NavHost(
        // Assign the navigator to the NavHost
        navigator = navigator,
        // Navigation transition for the scenes in this NavHost, this is optional
        navTransition = NavTransition(),
        // The start destination
        initialRoute = "/description",
    ) {
        // Define a scene to the navigation graph
        scene(
            // Scene's route path
            route = "/description",
            // Navigation transition for this scene, this is optional
            navTransition = NavTransition(),
        ) {
            VideoDescription(videoInfo) {

            }
        }

        scene(
            // Scene's route path
            route = "/comment",
            // Navigation transition for this scene, this is optional
            navTransition = NavTransition(),
        ) {
            CommentsPage(videoId) {

            }
        }

        scene(
            // Scene's route path
            route = "/comment/{id}",
            // Navigation transition for this scene, this is optional
            navTransition = NavTransition(),
        ) {
            val id = it.path<Long>("id")!!
            CommentReplyPage(id, videoId.toLong())
        }
    }
}