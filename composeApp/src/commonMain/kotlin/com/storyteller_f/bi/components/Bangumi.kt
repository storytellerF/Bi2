package com.storyteller_f.bi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.storyteller_f.bi.data.*
import com.storyteller_f.bi.entity.BangumiInfo
import com.storyteller_f.bi.network.LoadingHandler
import com.storyteller_f.bi.network.Service.bangumiInfo
import com.storyteller_f.bi.network.bangumiPlayerRepository
import com.storyteller_f.bi.network.request
import com.storyteller_f.bi.player.PlayerSession
import com.storyteller_f.bi.player.rememberPlayerService
import com.storyteller_f.bi.safeSubList
import com.storyteller_f.bi.ui.StateView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BangumiViewModel(val id: String, private val seasonId: String) : ViewModel() {
    val bangumiHandler = LoadingHandler<BangumiInfo?>(::refresh)

    private val current = MutableStateFlow(id)

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentVideoRepository = current.flatMapLatest { c ->
        bangumiHandler.data.map { bangumiInfo ->
            val v = bangumiInfo?.episodes?.firstOrNull {
                it.aid == c
            }
            if (v != null) {
                bangumiPlayerRepository(v, bangumiInfo)
            } else {
                null
            }
        }
    }

    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            request(bangumiHandler) {
                bangumiInfo(seasonId)
            }
        }
    }
}

@Composable
fun BangumiPage(
    sessionPack: PlayerSession.BangumiSession
) {
    val id = sessionPack.id
    val seasonId = sessionPack.seasonId
    val bangumiViewModel =
        customViewModel(BangumiViewModel::class, keys = listOf(id, seasonId)) {
            BangumiViewModel(id, seasonId)
        }
    val info by bangumiViewModel.bangumiHandler.data.collectAsState()
    val bangumiPlayerRepository by bangumiViewModel.currentVideoRepository.collectAsState(null)
    val playerKit = rememberPlayerService(
        videoPlayerRepository = bangumiPlayerRepository,
        initProgress = 0
    )
    StateView(bangumiViewModel.bangumiHandler) {
        Column {
            Text(text = "id $id season $seasonId")
            VideoFrame(
                playerKit,
                aspectRatio = true,
                switchFullscreenMode = null
            )
            BangumiPageNavHost(info, id)
        }
    }
}

@Composable
fun BangumiDescription(info: BangumiInfo?, toComment: () -> Unit = {}) {
    Surface {
        if (info != null) {
            Text(
                "comment",
                modifier = Modifier.clickable {
                    toComment()
                }
            )
            Column {
                Text(text = info.title)
                val main = info.episodes.take(info.totalEp)
                val other = info.episodes.safeSubList(info.totalEp, info.episodes.size)
                val episodeModifier =
                    Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp).sizeIn(maxWidth = 80.dp)
                val color = MaterialTheme.colorScheme.onPrimaryContainer
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(main.size) {
                        Text(text = main[it].safeTitle, color = color, modifier = episodeModifier)
                    }
                }
                LazyRow {
                    items(other.size) {
                        Text(text = other[it].safeTitle, color = color, modifier = episodeModifier)
                    }
                }
            }
        }
    }
}

@Composable
fun BangumiPageNavHost(videoInfo: BangumiInfo?, videoId: String) {
    val navigator = rememberNavController()
    NavHost(
        navController = navigator,
        startDestination = "/home",
    ) {
        composable(
            route = "/description",
        ) {
            BangumiDescription(videoInfo)
        }

        composable(
            route = "/comment",
        ) {
            CommentsPage(videoId)
        }

        composable(
            route = "/comment/{id}",
        ) {
            it.arguments?.getLong("id")?.let { it1 -> CommentReplyPage(it1, videoId.toLong()) }
        }
    }
}
