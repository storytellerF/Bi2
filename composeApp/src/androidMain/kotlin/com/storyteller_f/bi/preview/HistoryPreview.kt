package com.storyteller_f.bi.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.storyteller_f.bi.components.HistoryItem
import com.storyteller_f.bi.network.HistoryVideoItem

class VideoItemProvider : PreviewParameterProvider<HistoryVideoItem> {
    override val values: Sequence<HistoryVideoItem>
        get() = sequence {
            yield(HistoryVideoItem("", "", 0L, 0L, 0, "test", "type"))
        }

}

@Preview
@Composable
fun HistoryVideoItemPreview(@PreviewParameter(VideoItemProvider::class) item: HistoryVideoItem) {
    HistoryItem(item)
}