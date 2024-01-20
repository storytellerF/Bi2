package com.storyteller_f.bi.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.*
import app.cash.paging.compose.collectAsLazyPagingItems
import com.storyteller_f.bi.data.customViewModel
import com.storyteller_f.bi.entity.SearchBangumiInfo
import com.storyteller_f.bi.entity.SearchUpperInfo
import com.storyteller_f.bi.entity.SearchVideoInfo
import com.storyteller_f.bi.entity.UserInfo
import com.storyteller_f.bi.gs.UserInfoState
import com.storyteller_f.bi.network.Service
import com.storyteller_f.bi.network.Service.bangumiSearchResultInfo
import com.storyteller_f.bi.network.Service.searchVideo
import com.storyteller_f.bi.ui.BasicPagingList
import com.storyteller_f.bi.ui.RemoteImage
import com.storyteller_f.bi.ui.StandBy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    userInfo: UserInfo? = null,
    dockMode: Boolean = false,
    playVideo: (String?, String?, String, Long) -> Unit = { _, _, _, _ -> }
) {
    val viewModel = customViewModel(VideoSearchViewModel::class)

    var activated by remember {
        mutableStateOf(false)
    }
    var input by remember {
        mutableStateOf(viewModel.keyword.value)
    }

    CombinedSearchBar(
        dockMode,
        input,
        {
            input = it
        },
        {
            viewModel.keyword.value = it
        },
        activated,
        {
            activated = it
        },
        @Composable {
            Text(text = "search")
        },
        @Composable {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back",
                modifier = Modifier.clickable {
                    activated = false
                }
            )
        },
        @Composable {
            Row(modifier = Modifier.padding(end = 8.dp)) {
                if (activated && input.isNotEmpty()) {
                    Icon(
                        Icons.Filled.Clear,
                        contentDescription = "clear",
                        modifier = Modifier.clickable {
                            input = ""
                        }
                    )
                }
                if (!activated && userInfo != null) HomeAvatar(userInfo)
            }
        },
        modifier,
        {
            SearchContent(viewModel, playVideo)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CombinedSearchBar(
    dockMode: Boolean,
    input: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    activated: Boolean,
    onActiveChange: (Boolean) -> Unit,
    placeholder: @Composable () -> Unit,
    leadingIcon: @Composable () -> Unit,
    trailingIcon: @Composable () -> Unit,
    modifier: Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    if (dockMode) {
        DockedSearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = input,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    expanded = activated,
                    onExpandedChange = onActiveChange,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                )
            },
            expanded = activated,
            onExpandedChange = onActiveChange,
            modifier = modifier,
            content = content,
        )
    } else {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = input,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    expanded = activated,
                    onExpandedChange = onActiveChange,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                )
            },
            expanded = activated,
            onExpandedChange = onActiveChange,
            modifier = modifier,
            content = content,
        )
    }
}

@Composable
@OptIn(
    ExperimentalMaterial3Api::class
)
private fun HomeAvatar(
    userInfo: UserInfo,
) {
    val coverSize = Modifier
        .padding(start = 8.dp)
        .size(30.dp)
    var showPopup by remember {
        mutableStateOf(false)
    }
    StandBy(coverSize) {
        RemoteImage(
            model = userInfo.face,
            contentDescription = "avatar",
            modifier = coverSize.clickable {
                showPopup = true
            }
        )
    }
    // todo decorFitsSystemWindows
    if (showPopup) {
        BasicAlertDialog(onDismissRequest = { showPopup = false }) {
            Surface(shape = RoundedCornerShape(20.dp)) {
                AvatarContent(userInfo)
            }
        }
    }
}

@Composable
private fun AvatarContent(userInfo: UserInfo? = null) {
    Column {
        Spacer(Modifier.height(12.dp))
        UserBanner(u = userInfo)
        Spacer(Modifier.height(12.dp))
        NavigationDrawerItem(label = { Text(text = "Setting") }, icon = {
            Icon(Icons.Filled.Settings, contentDescription = "setting")
        }, selected = false, onClick = {
        })
        NavigationDrawerItem(label = { Text(text = "Logout") }, icon = {
            Icon(Icons.Filled.Close, contentDescription = "logout")
        }, selected = false, onClick = {
            UserInfoState.logout()
        })
    }
}

@Composable
private fun SearchContent(
    viewModel: VideoSearchViewModel,
    playVideo: (String?, String?, String, Long) -> Unit,
) {
    var selected by remember {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        3
    }

    val coroutineScope = rememberCoroutineScope()
    TabRow(selectedTabIndex = selected) {
        val l = listOf("video", "bangumi", "up")
        l.forEachIndexed { i, e ->
            Tab(selected = selected == i, onClick = {
                selected = i
                coroutineScope.launch {
                    pagerState.scrollToPage(i)
                }
            }) {
                Text(text = e, modifier = Modifier.padding(vertical = 12.dp))
            }
        }
    }
    HorizontalPager(state = pagerState) {
        SearchPageBuilder(it, viewModel, playVideo)
    }
}

@Composable
private fun SearchPageBuilder(
    it: Int,
    viewModel: VideoSearchViewModel,
    playVideo: (String?, String?, String, Long) -> Unit
) {
    when (it) {
        0 -> {
            val pagingItems = viewModel.videoResult.collectAsLazyPagingItems()
            BasicPagingList(pagingItems, {
                it.mid
            }) { item ->
                VideoItem(
                    item?.cover,
                    item?.title.orEmpty(),
                    item?.author.orEmpty()
                ) {
                    playVideo(item?.param, item?.param, "archive", 0)
                }
            }
        }

        1 -> {
            val pagingItems =
                viewModel.bangumiResult.collectAsLazyPagingItems()
            BasicPagingList(pagingItems, {
                it.uri
            }) { item ->
                VideoItem(
                    item?.cover,
                    item?.title.orEmpty(),
                    item?.catDesc.orEmpty()
                ) {
//                                    current.playVideo(item?.param)
                }
            }
        }

        2 -> {
            val pagingItems = viewModel.upResult.collectAsLazyPagingItems()
            BasicPagingList(pagingItems, {
                it.uri
            }) { item ->
                UpItem(item)
            }
        }
    }
}

@Composable
fun UpItem(item: SearchUpperInfo?) {
    Row(modifier = Modifier.padding(8.dp)) {
        val modifier = Modifier.size(40.dp)
        StandBy(modifier) {
            val cover = item?.cover
            RemoteImage(
                model = if (cover != null) "$cover@200w_200h" else null,
                contentDescription = "cover",
                modifier = modifier
            )
        }
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(text = item?.title.orEmpty())
            Text(text = item?.sign.orEmpty())
        }
    }
}

class VideoSearchViewModel : ViewModel() {
    val keyword = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val videoResult = keyword.flatMapLatest {
        Pager(
            PagingConfig(pageSize = 20)
        ) {
            SearchSource(it)
        }.flow
            .cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val bangumiResult = keyword.flatMapLatest {
        Pager(
            PagingConfig(pageSize = 20)
        ) {
            SearchBangumiSource(it)
        }.flow
            .cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val upResult = keyword.flatMapLatest {
        Pager(
            PagingConfig(pageSize = 20)
        ) {
            SearchUpSource(it)
        }.flow
            .cachedIn(viewModelScope)
    }
}

class SearchSource(private val keyword: String) : PagingSource<Int, SearchVideoInfo>() {
    override fun getRefreshKey(state: PagingState<Int, SearchVideoInfo>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchVideoInfo> {
        val key = params.key
        return if (keyword == "") {
            LoadResult.Error(Exception("输入内容开始搜索"))
        } else {
            val pageNum = key ?: 1
            searchVideo(keyword, pageNum, params.loadSize).fold(onSuccess = {
                val archive = it.data!!.items.archive.orEmpty()
                val nextKey = if (archive.isEmpty()) null else pageNum + 1
                LoadResult.Page(archive, null, nextKey)
            }, onFailure = {
                LoadResult.Error(it)
            })
        }
    }
}

class SearchUpSource(private val keyword: String) : PagingSource<Int, SearchUpperInfo>() {
    override fun getRefreshKey(state: PagingState<Int, SearchUpperInfo>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchUpperInfo> {
        if (keyword == "") {
            return LoadResult.Error(Exception("不能为空"))
        } else {
            val pageNum = params.key ?: 1
            val pageSize = params.loadSize
            val res = Service.searchUpper(keyword, pageSize, pageNum)
            return res.fold(onSuccess = {
                val archive = it.data!!.items.orEmpty()
                val nextKey = if (archive.isEmpty()) null else pageNum + 1
                LoadResult.Page(archive, null, nextKey)
            }, onFailure = {
                LoadResult.Error(it)
            })
        }
    }
}

class SearchBangumiSource(private val keyword: String) : PagingSource<Int, SearchBangumiInfo>() {
    override fun getRefreshKey(state: PagingState<Int, SearchBangumiInfo>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchBangumiInfo> {
        return if (keyword == "") {
            LoadResult.Error(Exception("不能为空"))
        } else {
            val key = params.key
            val pageNum = key ?: 1
            val res = bangumiSearchResultInfo(keyword, params.loadSize, pageNum)
            res.fold(onSuccess = {
                val archive = it.data!!.items.orEmpty()
                val nextKey = if (archive.isEmpty()) null else pageNum + 1
                LoadResult.Page(archive, null, nextKey)
            }, onFailure = {
                LoadResult.Error(it)
            })
        }
    }
}
