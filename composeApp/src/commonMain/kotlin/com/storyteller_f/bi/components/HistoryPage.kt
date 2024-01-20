package com.storyteller_f.bi.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import bilibili.app.interfaces.v1.CursorItem
import com.storyteller_f.bi.LocalAppNav
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
        CardConstrains(cardWidth = 300) { count ->
            LazyVerticalGrid(
                GridCells.Fixed(count),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    count = lazyItems.itemCount,
                    key = lazyItems.itemKey {
                        it.oid.toString() + "" + it.kid.toString()
                    },
                    span = {
                        GridItemSpan(1)
                    }
                ) { index ->
                    val item = lazyItems[index]
                    if (item != null) {
                        HistoryItem(item)
                    }
                }
                bottomAppending(lazyItems, count)
            }
        }
    }
}

@Composable
fun CardConstrains(modifier: Modifier = Modifier, cardWidth: Int, content: @Composable (Int) -> Unit) {
    BoxWithConstraints(modifier) {
        val gridCount = (maxWidth / cardWidth.dp).toInt()
        content(gridCount)
    }
}

fun gcd(a: Int, b: Int): Int {
    return if (b == 0) a else gcd(b, a % b)
}

fun lcm(a: Int, b: Int): Int {
    return (a * b) / gcd(a, b)
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
    val appNav = LocalAppNav.current
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
        Column {
            Spacer(Modifier.height(4.dp))
            val coverModifier = Modifier
                .width(128.dp)
                .aspectRatio(16f / 9)
            StandBy(coverModifier) {
                val u = if (pic == null) null else "$pic@672w_378h_1c_"
                RemoteImage(u, contentDescription = null, modifier = coverModifier)
            }
        }
        Column(modifier = Modifier.padding(start = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = text, maxLines = 2, style = MaterialTheme.typography.titleSmall)
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    }
}
