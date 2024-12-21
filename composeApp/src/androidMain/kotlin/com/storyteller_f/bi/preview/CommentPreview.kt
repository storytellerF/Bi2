package com.storyteller_f.bi.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import bilibili.main.community.reply.v1.Content
import bilibili.main.community.reply.v1.Member
import bilibili.main.community.reply.v1.ReplyInfo
import com.storyteller_f.bi.components.CommentItem

class CommentReplyListPreviewProvider : PreviewParameterProvider<List<ReplyInfo>> {
    override val values: Sequence<List<ReplyInfo>>
        get() = sequence {
            yield(
                buildList {
                    add(buildUserComment("不知名用户1"))
                    add(buildUserComment("不知名用户2", parent = 1L))
                    add(buildUserComment("不知名用户3"))
                }.map {
                    it
                }
            )
        }
}

class CommentItemPreviewProvider : PreviewParameterProvider<ReplyInfo> {
    override val values: Sequence<ReplyInfo>
        get() = sequence {
            yield(buildUserComment("不知名用户1"))
            yield(buildUserComment("mock 2"))
            yield(
                buildUserComment(
                    "you may not use this file except in compliance with the License. " +
                        "You may obtain a copy of the License at",
                )
            )
        }
}

fun buildUserComment(userName: String, parent: Long = 0L): ReplyInfo =
    ReplyInfo(
        member = Member(name = userName),
        content = Content(message = "评论消息内容"),
        parent = parent,
        like = 100
    )

@Preview
@Composable
private fun PreviewCommentReplyList(@PreviewParameter(CommentReplyListPreviewProvider::class) data: List<ReplyInfo>) {
    Column {
        data.forEach {
            CommentItem(item = it, parent = 0L)
        }
    }
}

@Preview
@Composable
fun CommentReplyListPreview(@PreviewParameter(CommentItemPreviewProvider::class) info: ReplyInfo) {
    CommentItem(info)
}
