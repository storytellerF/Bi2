package com.storyteller_f.bi.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.storyteller_f.bi.components.MediaListContainer
import com.storyteller_f.bi.entity.MediaListInfo

class MediaListContainerPreviewProvider : PreviewParameterProvider<MediaListInfo> {
    override val values: Sequence<MediaListInfo>
        get() = sequence {
            yield(
                MediaListInfo(
                    "",
                    1,
                    0L,
                    1,
                    0,
                    111,
                    "title",
                    0,
                    1,
                    0L,
                    0,
                    "title",
                    1,
                )
            )
        }
}

@Preview
@Composable
fun FavoritePreview(@PreviewParameter(MediaListContainerPreviewProvider::class) info: MediaListInfo) {
    MediaListContainer(info)
}
