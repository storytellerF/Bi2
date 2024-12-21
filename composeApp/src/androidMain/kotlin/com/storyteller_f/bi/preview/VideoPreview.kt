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
                    "867109523",
                    1,
                    "",
                    0L,
                    1,
                    0.0,
                    "",
                    1,
                    1,
                    "",
                    VideoOwnerInfo("", "", ""),
                    listOf(VideoPageInfo("", "", 1, "", 1, "part", "", "")),
                    "",
                    0L,
                    null,
                    VideoReqUserInfo(1, 1, 1, 1, 1),
                    null,
                    null,
                    VideoStatInfo("", ""),
                    1,
                    listOf(
                        VideoTagInfo(1, "", 1, 1, 1, 1, 1, 0.0, "tag name"),
                        VideoTagInfo(1, "", 1, 1, 1, 1, 1, 0.1, "tag name")
                    ),
                    1,
                    "video title",
                    "",
                    1,
                    null
                )
            )
        }
}

@Preview
@Composable
fun VideoDescriptionPreview(@PreviewParameter(VideoInfoPreviewProvider::class) videoInfo: VideoInfo) {
    VideoDescription(videoInfo)
}
