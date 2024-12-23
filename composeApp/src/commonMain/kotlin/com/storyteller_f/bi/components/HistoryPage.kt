package com.storyteller_f.bi.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import bilibili.app.interfaces.v1.CursorItem
import com.storyteller_f.bi.LOCALAppNav
import com.storyteller_f.bi.data.PagingViewModel
import com.storyteller_f.bi.data.SimplePagingSource
import com.storyteller_f.bi.data.customViewModel
import com.storyteller_f.bi.network.Service.historyResult
import com.storyteller_f.bi.network.cover
import com.storyteller_f.bi.network.progress
import com.storyteller_f.bi.network.type
import com.storyteller_f.bi.player.PlayerSession
import com.storyteller_f.bi.ui.RemoteImage
import com.storyteller_f.bi.ui.StandBy
import com.storyteller_f.bi.ui.StateView
import com.storyteller_f.bi.ui.bottomAppending

// todo 增加搜索功能
class HistoryViewModel :
    PagingViewModel<Pair<Long, Int>, CursorItem>({ SimplePagingSource(::historyResult) })

@Composable
fun HistoryPage() {
    val viewModel = customViewModel(HistoryViewModel::class)

    val lazyItems = viewModel.flow.collectAsLazyPagingItems()

    StateView(pagingItems = lazyItems) {
        LazyColumn {
            items(
                count = lazyItems.itemCount,
                key = lazyItems.itemKey {
                    it.oid.toString() + "" + it.kid.toString()
                },
                contentType = lazyItems.itemContentType()
            ) { index ->
                val item = lazyItems[index]
                if (item != null) {
                    HistoryItem(item)
                }
            }
            bottomAppending(lazyItems)
        }
    }
}

@Composable
fun HistoryItem(
    item: CursorItem
) {
    val text = item.title
    val progress = item.progress()
    val kid = item.kid
    val oid = item.oid
    val business = item.business
    val pic = item.cover()
    val label = "$oid $kid ${item.type()} $business $progress"
    val appNav = LOCALAppNav.current
    VideoItem(pic, text, label) {
        appNav.gotoVideo(PlayerSession.VideoSession(kid.toString(), business, progress))
    }
}

@Composable
fun VideoItem(
    pic: String? = null,
    text: String = "text",
    label: String = "label",
    watchVideo: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                watchVideo()
            }
    ) {
        val coverModifier = Modifier
            .width((16 * 8).dp)
            .height((9 * 8).dp)
        StandBy(coverModifier) {
            val u = if (pic == null) null else "$pic@672w_378h_1c_"
            RemoteImage(u, contentDescription = null, modifier = coverModifier)
        }
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(text = text, maxLines = 2)
            Text(text = label)
        }
    }
}
