package com.storyteller_f.bi.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.storyteller_f.bi.components.UpItem
import com.storyteller_f.bi.entity.SearchUpperInfo

class UpItemPreviewProvider : PreviewParameterProvider<SearchUpperInfo?> {
    override val values: Sequence<SearchUpperInfo?>
        get() = sequence {
            yield(
                SearchUpperInfo(
                    0,
                    "test",
                    0,
                    "",
                    "",
                    "",
                    1,
                    "sign",
                    1,
                    ""
                )
            )
        }
}

@Preview
@Composable
fun UpItemPreview(@PreviewParameter(UpItemPreviewProvider::class) item: SearchUpperInfo) {
    UpItem(item)
}
