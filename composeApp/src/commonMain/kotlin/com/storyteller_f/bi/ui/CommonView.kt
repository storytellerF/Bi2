package com.storyteller_f.bi.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import com.seiko.imageloader.rememberImagePainter
import com.storyteller_f.bi.LOCALAppNav
import com.storyteller_f.bi.gs.UserInfoState
import com.storyteller_f.bi.network.LoadingHandler
import com.storyteller_f.bi.network.LoadingState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Center(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        content()
    }
}

@Composable
fun StateView(state: LoadingState?, refresh: () -> Unit, content: @Composable () -> Unit) {
    when (state) {
        null -> Center {
            Text(text = "Waiting")
        }

        is LoadingState.Loading -> Center {
            Text(text = "Loading")
        }

        is LoadingState.Error -> Center {
            val text = when (state.e) {
                is Exception -> state.e.message
                is Error -> state.e.message
                else -> state.e.message
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = text.toString())
                Button({
                    refresh()
                }) {
                    Text("Retry")
                }
            }
        }

        is LoadingState.Done -> if (state.itemCount == 0) {
            Center {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Empty")
                    Button({
                        refresh()
                    }) {
                        Text("Refresh")
                    }
                }
            }
        } else {
            content()
        }
    }
}

const val REFRESH_AT_LEAST_DELAY = 300L

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StateView(handler: LoadingHandler<*>, content: @Composable () -> Unit) {
    val state by handler.state.collectAsState()
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        refreshScope.launch {
            refreshing = true
            handler.refresh()
        }
    })
    LaunchedEffect(key1 = refreshing, key2 = state) {
        delay(REFRESH_AT_LEAST_DELAY)
        if (refreshing && state !is LoadingState.Loading) refreshing = false
    }
    Box(modifier = Modifier.pullRefresh(refreshState)) {
        StateView(state, {
            refreshScope.launch {
                handler.refresh()
            }
        }, content)
        PullRefreshIndicator(refreshing, refreshState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun UserHost(content: @Composable () -> Unit) {
    val u by UserInfoState.state.collectAsState()
    if (u == null) {
        Center {
            val appNav = LOCALAppNav.current
            Button(onClick = {
                appNav.gotoLogin()
            }) {
                Text(text = "Login")
            }
        }
    } else {
        content()
    }
}

fun <T : Any> LazyListScope.topRefreshing(lazyPagingItems: LazyPagingItems<T>) {
    if (lazyPagingItems.loadState.refresh == LoadStateLoading) {
        item {
            Text(
                text = "Waiting for items to load from the backend",
                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    }
}

fun <T : Any> LazyListScope.bottomAppending(lazyPagingItems: LazyPagingItems<T>) {
    if (lazyPagingItems.loadState.append == LoadStateLoading) {
        item {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : Any> StateView(pagingItems: LazyPagingItems<T>, function: @Composable () -> Unit) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        refreshScope.launch {
            refreshing = true
            pagingItems.refresh()
        }
    })
    val refresh = pagingItems.loadState.refresh
    LaunchedEffect(key1 = refreshing, key2 = refresh) {
        // 增加延时，确保真正进入刷新状态
        delay(REFRESH_AT_LEAST_DELAY)
        if (refreshing && refresh !is LoadStateLoading) refreshing = false
    }
    Box(modifier = Modifier.pullRefresh(refreshState)) {
        StateView(refresh, pagingItems.itemCount, {
            pagingItems.refresh()
        }, function)
        PullRefreshIndicator(refreshing, refreshState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun StateView(state: app.cash.paging.LoadState?, count: Int = 1, refresh: () -> Unit, content: @Composable () -> Unit) {
    val loadingState = when (state) {
        null -> null

        is LoadStateLoading -> LoadingState.Loading("loading")

        is LoadStateError -> LoadingState.Error(state.error)

        is LoadStateNotLoading -> LoadingState.Done(count)
    }
    StateView(loadingState, refresh, content)
}

@Composable
fun StandBy(modifier: Modifier, me: @Composable () -> Unit) {
    if (LocalInspectionMode.current) {
        Box(modifier.background(MaterialTheme.colorScheme.background))
    } else {
        me()
    }
}

@Composable
fun <T : Any> BasicPagingList(
    lazyPagingItems: LazyPagingItems<T>,
    content: @Composable (T?) -> Unit
) {
    StateView(lazyPagingItems) {
        LazyColumn {
            topRefreshing(lazyPagingItems)
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey(),
                contentType = lazyPagingItems.itemContentType()
            ) { index ->
                val item = lazyPagingItems[index]
                content(item)
            }
            bottomAppending(lazyPagingItems)
        }
    }
}

@Composable
fun RemoteImage(model: String?, contentDescription: String?, modifier: Modifier) {
    if (model != null) {
        val painter = rememberImagePainter(
            if ("://" in model) {
                model.replace("http://", "https://")
            } else {
                "https:$model"
            }
        )
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
}
