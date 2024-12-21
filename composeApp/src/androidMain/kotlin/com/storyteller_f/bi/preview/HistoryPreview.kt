package com.storyteller_f.bi.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import bilibili.app.interfaces.v1.CursorItem
import com.storyteller_f.bi.components.HistoryItem

class VideoItemProvider : PreviewParameterProvider<CursorItem> {
    override val values: Sequence<CursorItem>
        get() = sequence {
            yield(CursorItem())
        }
}

@Preview
@Composable
fun HistoryVideoItemPreview(@PreviewParameter(VideoItemProvider::class) item: CursorItem) {
    HistoryItem(item)
}
