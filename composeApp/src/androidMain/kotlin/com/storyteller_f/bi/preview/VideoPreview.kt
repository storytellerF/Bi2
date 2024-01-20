package com.storyteller_f.bi.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.storyteller_f.bi.components.VideoDescription
import com.storyteller_f.bi.entity.*

class VideoInfoPreviewProvider : PreviewParameterProvider<VideoInfo> {
    override val values: Sequence<VideoInfo>
        get() = sequence {
            yield(
                VideoInfo(
                    867109523,
                    "",
                    0L,
                    0.0,
                    "",
                    1,
                    listOf(VideoPageInfo(1, "part")),
                    "",
                    0L,
                    listOf(
                        VideoTagInfo(0.0, "tag name"),
                        VideoTagInfo(0.1, "tag name")
                    ),
                    "video title"
                )
            )
        }
}

@Preview
@Composable
fun VideoDescriptionPreview(@PreviewParameter(VideoInfoPreviewProvider::class) videoInfo: VideoInfo) {
    VideoDescription(videoInfo)
}
