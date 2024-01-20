package com.storyteller_f.bi.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.storyteller_f.bi.data.viewModel
import com.storyteller_f.bi.network.*
import com.storyteller_f.bi.network.Service.playList
import com.storyteller_f.bi.ui.StateView
import com.storyteller_f.bi.player.PlayerSession
import kotlinx.coroutines.launch
import com.storyteller_f.bi.data.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

@Composable
fun PlaylistPage(openVideo: (PlayerSession) -> Unit = { }) {
    val viewModel = viewModel(ToBePlayedViewModel::class)
    val list by viewModel.datum.collectAsState()
    val data = list?.list.orEmpty()
    StateView(viewModel.handler) {
        LazyColumn {
            items(data, {
                it.aid.toString() + " " + it.bvid
            }) {
                VideoItem(it.pic, it.title, "${it.aid} ${it.bvid} ${it.cid} ${it.tid}") {
                    openVideo(PlayerSession.VideoSession(it.bvid, "archive", 0L))
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
