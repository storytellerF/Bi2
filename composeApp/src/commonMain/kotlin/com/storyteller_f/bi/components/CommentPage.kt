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
import app.cash.paging.compose.itemKey
import bilibili.main.community.reply.v1.ReplyInfo
import com.storyteller_f.bi.data.*
import com.storyteller_f.bi.network.CommentReplyCursor
import com.storyteller_f.bi.network.Service
import com.storyteller_f.bi.ui.*

class CommentViewModel(oid: String) : PagingViewModel<Long, ReplyInfo>({
    SimplePagingSource {
        Service.commentResult(oid, it)
    }
})

class CommentReplyViewModel(oid: Long, commentId: Long) :
    PagingViewModel<CommentReplyCursor, ReplyInfo>({
        SimplePagingSource {
            Service.commentReplyResult(oid, commentId, it)
        }
    })

@Composable
fun CommentsPage(videoId: String, viewComment: (Long) -> Unit = {}) {
    val model = customViewModel(CommentViewModel::class, keys = listOf("comments", videoId)) {
        set(VideoId, videoId)
    }
    CommentList(0, model.flow.collectAsLazyPagingItems(), viewComment)
}

@Composable
fun CommentReplyPage(cid: Long, oid: Long) {
    val model = customViewModel(CommentReplyViewModel::class, keys = listOf(cid, oid)) {
        set(VideoIdLong, oid)
        set(CommentId, cid)
    }
    CommentList(cid, pagingItems = model.flow.collectAsLazyPagingItems())
}

@Composable
private fun CommentList(
    parent: Long,
    pagingItems: LazyPagingItems<ReplyInfo>,
    viewComment: (Long) -> Unit = {}
) {
    StateView(pagingItems) {
        LazyColumn {
            topRefreshing(pagingItems)
            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey {
                    it.id
                },
            ) { index ->
                val item = pagingItems[index]
                if (item != null) {
                    CommentItem(item = item, viewComment, parent)
                }
            }
            bottomAppending(pagingItems)
        }
    }
}

@Composable
fun CommentItem(
    item: ReplyInfo,
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
        val basic = item.member_v2?.basic
        Row {
            val modifier = Modifier.size(30.dp)
            StandBy(modifier) {
                RemoteImage(
                    model = basic?.face,
                    contentDescription = "avatar",
                    modifier = modifier
                )
            }
            if (basic != null) {
                Text(
                    text = basic.name,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    maxLines = 2
                )
            }
            Text(text = "like ${item.like} reply ${item.count}")
        }
        item.content?.message?.let { Text(text = it, modifier = Modifier.padding(top = 8.dp)) }
        Text(text = "${item.parent} ${item.dialog} ${item.id} ${item.type}")
    }
}
