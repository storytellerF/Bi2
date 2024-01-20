package com.storyteller_f.bi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import bilibili.app.dynamic.v2.ModuleDynamicType
import com.storyteller_f.bi.LocalAppNav
import com.storyteller_f.bi.data.PagingViewModel
import com.storyteller_f.bi.data.customViewModel
import com.storyteller_f.bi.network.MomentsDataInfo
import com.storyteller_f.bi.network.Service.momentRequest
import com.storyteller_f.bi.network.loadResult
import com.storyteller_f.bi.player.PlayerSession
import com.storyteller_f.bi.ui.*

@Composable
fun MomentsPage() {
    val viewModel = customViewModel(MomentsViewModel::class)
    val pagingItems = viewModel.flow.collectAsLazyPagingItems()
    StateView(pagingItems = pagingItems) {
        CardConstrains(cardWidth = 300) { count ->
            LazyVerticalGrid(
                GridCells.Fixed(count),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                topRefreshing(pagingItems, count)
                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey {
                        it.dynamicContent.id
                    },
                ) { index ->
                    val item = pagingItems[index]
                    if (item != null) {
                        val appNav = LocalAppNav.current
                        MomentItem(item) {
                            appNav.gotoVideo(PlayerSession.VideoSession(it, "archive", 0))
                        }
                    }
                }
                bottomAppending(pagingItems, count)
            }
        }
    }
}

@Composable
fun MomentItem(
    momentsDataInfo: MomentsDataInfo,
    watchVideo: (String) -> Unit = {}
) {
    val authorSize = Modifier.size(40.dp)
    Column(
        modifier = Modifier.padding(8.dp).fillMaxWidth()
    ) {
        Row {
            StandBy(authorSize) {
                RemoteImage(model = momentsDataInfo.face, contentDescription = "", modifier = authorSize)
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = momentsDataInfo.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = momentsDataInfo.labelText,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        val dynamicContent = momentsDataInfo.dynamicContent
        VideoItem(dynamicContent.pic, dynamicContent.title, dynamicContent.remark.orEmpty()) {
            if (momentsDataInfo.dynamicType == ModuleDynamicType.mdl_dyn_archive.ordinal) {
                watchVideo(momentsDataInfo.dynamicContent.id)
            }
        }
        Row(modifier = Modifier.padding(start = 8.dp)) {
            ThumbUp(momentsDataInfo.stat.like.toString(), Icons.Filled.ThumbUp, "thumb up count")
            Spacer(modifier = Modifier.size(8.dp))
            ThumbUp(
                momentsDataInfo.stat.reply.toString(),
                Icons.Filled.Add,
                "reply count"
            )
            Spacer(modifier = Modifier.size(8.dp))
            ThumbUp(momentsDataInfo.stat.repost.toString(), Icons.Filled.Share, "repost")
        }
    }
}

@Composable
fun ThumbUp(
    text: String = "1",
    imageVector: ImageVector = Icons.Filled.ThumbUp,
    description: String = ""
) {
    Row(
        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = imageVector, contentDescription = description, modifier = Modifier.size(20.dp))
        Text(text = text, modifier = Modifier.padding(start = 8.dp))
    }
}

class MomentsViewModel : PagingViewModel<Pair<String, String>, MomentsDataInfo>({
    MomentsPagingSource()
})

class MomentsPagingSource : PagingSource<Pair<String, String>, MomentsDataInfo>() {
    override suspend fun load(
        params: LoadParams<Pair<String, String>>
    ): LoadResult<Pair<String, String>, MomentsDataInfo> {
        return momentRequest(params.key).loadResult()
    }

    override fun getRefreshKey(state: PagingState<Pair<String, String>, MomentsDataInfo>): Pair<String, String>? {
        return null
    }

    companion object
}
