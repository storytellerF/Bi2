package com.storyteller_f.bi.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import com.storyteller_f.bi.data.*
import com.storyteller_f.bi.entity.MediasInfo
import com.storyteller_f.bi.network.Service.favoriteDetail
import com.storyteller_f.bi.player.PlayerSession
import com.storyteller_f.bi.ui.StateView

@Composable
fun FavoriteDetailPage(id: Int, playVideo: (PlayerSession) -> Unit) {
    val detailViewModel = customViewModel(
        FavoriteDetailViewModel::class
    ) {
        set(FavoriteIdKey, id.toString())
    }
    val lazyPagingItems = detailViewModel.flow.collectAsLazyPagingItems()
    StateView(pagingItems = lazyPagingItems) {
        LazyColumn {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey {
                    it.id
                },
            ) { index ->
                val item = lazyPagingItems[index]
                VideoItem(
                    item?.cover.orEmpty(),
                    item?.title.orEmpty(),
                    item?.upper?.name.orEmpty()
                ) {
                    playVideo(PlayerSession.VideoSession(item?.id!!, "archive", 0))
                }
            }
        }
    }
}

class FavoriteDetailViewModel(id: String) : PagingViewModel<Int, MediasInfo>({
    FavoriteDetailSource(id)
})

class FavoriteDetailSource(val id: String) : PagingSource<Int, MediasInfo>() {
    override fun getRefreshKey(state: PagingState<Int, MediasInfo>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediasInfo> {
        if (id.isEmpty()) {
            return LoadResult.Error(Exception("id is empty"))
        }
        val lastPage = params.key ?: 0
        val currentPage = lastPage + 1
        val pageSize = params.loadSize
        return favoriteDetail(currentPage, pageSize, id).fold(onSuccess = { data ->
            val medias = data.`data`?.medias.orEmpty()
            LoadResult.Page(
                medias,
                null,
                if (medias.size < pageSize) null else currentPage + 1
            )
        }, onFailure = {
            LoadResult.Error(it)
        })
    }
}
