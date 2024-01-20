package com.storyteller_f.bi.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import com.storyteller_f.bi.ui.RemoteImage
import com.storyteller_f.bi.ui.StandBy
import com.storyteller_f.bi.ui.StateView
import com.storyteller_f.bi.data.*
import com.storyteller_f.bi.network.CommentInfo
import com.storyteller_f.bi.network.CommentReplyCursor
import com.storyteller_f.bi.network.Service
import com.storyteller_f.bi.ui.topRefreshing



class CommentViewModel(oid: String) : PagingViewModel<Long, CommentInfo>({
    SimplePagingSource {
        Service.commentResult(oid, it)
    }
})

class CommentReplyViewModel(oid: Long, commentId: Long) :
    PagingViewModel<CommentReplyCursor, CommentInfo>({
        SimplePagingSource {
            Service.commentReplyResult(oid, commentId, it)
        }
    })


@Composable
fun CommentsPage(videoId: String, viewComment: (Long) -> Unit = {}) {
    val model = viewModel(CommentViewModel::class) {
        CommentViewModel(videoId)
    }
    CommentList(0, model.flow.collectAsLazyPagingItems(), viewComment)
}

@Composable
fun CommentReplyPage(cid: Long, oid: Long) {
    val model = viewModel(CommentReplyViewModel::class, listOf(cid, oid)) {
        CommentReplyViewModel(oid, cid)
    }
    CommentList(cid, pagingItems = model.flow.collectAsLazyPagingItems())
}

@Composable
private fun CommentList(
    parent: Long,
    pagingItems: LazyPagingItems<CommentInfo>,
    viewComment: (Long) -> Unit = {}
) {
    StateView(pagingItems.loadState.refresh) {
        LazyColumn {
            topRefreshing(pagingItems)
            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey(),
                contentType = pagingItems.itemContentType()
            ) { index ->
                val item = pagingItems[index]
                if (item != null)
                    CommentItem(item = item, viewComment, parent)
            }
        }
    }
}


@Composable
fun CommentItem(
    item: CommentInfo,
    viewComment: (Long) -> Unit = {},
    parent: Long = 0,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .padding(start = if (parent == item.parent) 0.dp else 24.dp)
            .fillMaxWidth()
            .clickable {
                viewComment(item.id)
            }
    ) {
        Row {
            val modifier = Modifier.size(30.dp)
            StandBy(modifier) {
                RemoteImage(
                    model = item.face,
                    contentDescription = "avatar",
                    modifier = modifier
                )
            }
            Text(
                text = item.name,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                maxLines = 2
            )
            Text(text = "like ${item.like} reply ${item.count}")
        }
        Text(text = item.message, modifier = Modifier.padding(top = 8.dp))
        Text(text = "${item.parent} ${item.dialog} ${item.id} ${item.type}")
    }
}

