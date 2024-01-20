package com.storyteller_f.bi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import com.storyteller_f.bi.LocalAppNav
import com.storyteller_f.bi.data.PagingViewModel
import com.storyteller_f.bi.data.customViewModel
import com.storyteller_f.bi.entity.MediaListInfo
import com.storyteller_f.bi.gs.UserInfoState
import com.storyteller_f.bi.network.Service.favoriteList
import com.storyteller_f.bi.ui.RemoteImage
import com.storyteller_f.bi.ui.StandBy
import com.storyteller_f.bi.ui.StateView

@Composable
fun FavoritePage() {
    val favoriteViewModel = customViewModel(FavoriteViewModel::class)
    val pagingItems = favoriteViewModel.flow.collectAsLazyPagingItems()
    StateView(pagingItems) {
        CardConstrains(cardWidth = 150) {
            LazyVerticalGrid(GridCells.Fixed(it)) {
                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey {
                        it.id
                    },
                ) {
                    pagingItems[it]?.let { info -> MediaListContainer(info) }
                }
            }
        }
    }
}

@Composable
fun MediaListContainer(
    mediaListInfo: MediaListInfo
) {
    val appNav = LocalAppNav.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.clickable {
            appNav.gotoFavorite(mediaListInfo.id)
        }
    ) {
        val modifier = Modifier.aspectRatio(16f / 9)
        StandBy(modifier) {
            RemoteImage(
                model = "${mediaListInfo.cover}@672w_378h_1c_",
                contentDescription = "cover",
                modifier = modifier
            )
        }

        Text(
            text = mediaListInfo.title,
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        )
    }
}

class FavoriteViewModel : PagingViewModel<Int, MediaListInfo>({
    FavoriteSource(UserInfoState.state.value?.mid?.toString()?.trim().orEmpty())
})

class FavoriteSource(private val mid: String) : PagingSource<Int, MediaListInfo>() {
    override fun getRefreshKey(state: PagingState<Int, MediaListInfo>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaListInfo> {
        return if (mid.isEmpty()) {
            LoadResult.Error(Exception("mid 不合法"))
        } else {
            val key = params.key ?: 1
            favoriteList(key, mid, params.loadSize).fold(onSuccess = { res ->
                val data = res.data!!
                LoadResult.Page(
                    data.list,
                    prevKey = null,
                    nextKey = if (data.hasMore) key + 1 else null
                )
            }, onFailure = {
                LoadResult.Error(it)
            })
        }
    }
}
