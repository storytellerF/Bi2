package com.storyteller_f.bi.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.storyteller_f.bi.components.MomentItem
import com.storyteller_f.bi.network.DynamicContentInfo
import com.storyteller_f.bi.network.MomentsDataInfo
import com.storyteller_f.bi.network.MomentsDesc
import com.storyteller_f.bi.network.MomentsStat

class MomentsPreviewProvider : PreviewParameterProvider<MomentsDataInfo> {
    override val values: Sequence<MomentsDataInfo>
        get() = sequence {
            yield(
                MomentsDataInfo(
                    MomentsDesc("desc"),
                    DynamicContentInfo(
                        "i",
                        pic = "https://i0.hdslb.com/bfs/face/member/noface.jpg",
                        title = "视频标题",
                    ),
                    0,
                    "up name",
                    "https://i0.hdslb.com/bfs/face/member/noface.jpg",
                    "labelText",
                    "",
                    MomentsStat(99, 99, 99)
                )
            )
        }
}

@Preview
@Composable
fun MomentsPreview(@PreviewParameter(MomentsPreviewProvider::class) info: MomentsDataInfo) {
    MomentItem(info)
}
