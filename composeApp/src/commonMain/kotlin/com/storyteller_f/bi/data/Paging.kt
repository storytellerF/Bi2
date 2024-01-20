package com.storyteller_f.bi.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.*
import com.storyteller_f.bi.entity.PagingData
import com.storyteller_f.bi.network.loadResult

abstract class PagingViewModel<K : Any, V : Any>(sourceBuilder: () -> PagingSource<K, V>) :
    ViewModel() {
    val flow = Pager(
        PagingConfig(pageSize = 20),
        pagingSourceFactory = sourceBuilder
    ).flow
        .cachedIn(viewModelScope)
}

class SimplePagingSource<KEY : Any, DATUM : Any>(val service: suspend (KEY?) -> Result<PagingData<KEY, DATUM>>) :
    PagingSource<KEY, DATUM>() {
    override suspend fun load(params: LoadParams<KEY>): LoadResult<KEY, DATUM> {
        return service(params.key).loadResult()
    }

    override fun getRefreshKey(state: PagingState<KEY, DATUM>): KEY? {
        return null
    }

    companion object
}
