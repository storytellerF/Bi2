package com.storyteller_f.bi.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.storyteller_f.bi.LocalAppNav
import com.storyteller_f.bi.data.customViewModel
import com.storyteller_f.bi.entity.VideoDatumList
import com.storyteller_f.bi.network.*
import com.storyteller_f.bi.network.Service.playList
import com.storyteller_f.bi.player.PlayerSession
import com.storyteller_f.bi.ui.StateView
import kotlinx.coroutines.launch

@Composable
fun PlaylistPage() {
    val viewModel = customViewModel(ToBePlayedViewModel::class)
    val list by viewModel.datum.collectAsState()
    val data = list?.list.orEmpty()
    val appNav = LocalAppNav.current
    StateView(viewModel.handler) {
        CardConstrains(cardWidth = 300) { count ->
            LazyVerticalGrid(
                GridCells.Fixed(count),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(data, {
                    it.aid.toString() + " " + it.bvid
                }) {
                    VideoItem(it.pic, it.title, "${it.aid} ${it.bvid} ${it.cid} ${it.tid}") {
                        appNav.gotoVideo(PlayerSession.VideoSession(it.bvid, "archive", 0L))
                    }
                }
            }
        }
    }
}

class ToBePlayedViewModel : ViewModel() {
    val handler = LoadingHandler<VideoDatumList?>(::load)
    val state = handler.state
    val datum = handler.data

    init {
        load()
    }

    private fun load() {
        state.loading()
        viewModelScope.launch {
            playList().fold(onSuccess = {
                datum.value = it.data
                state.loaded()
            }, onFailure = {
                state.error(it)
            })
        }
    }
}
