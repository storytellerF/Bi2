package com.storyteller_f.bi.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.storyteller_f.bi.LOCALAppNav
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
    val appNav = LOCALAppNav.current
    StateView(viewModel.handler) {
        LazyColumn {
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
