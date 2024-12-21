package com.storyteller_f.bi.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.storyteller_f.bi.components.BangumiDescription
import com.storyteller_f.bi.entity.BangumiInfo
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class BangumiPreviewProvider : PreviewParameterProvider<BangumiInfo?> {
    @OptIn(ExperimentalSerializationApi::class)
    override val values: Sequence<BangumiInfo?>
        get() = sequence {
            val stream = this::class.java.classLoader?.getResourceAsStream("bangumi.json")
            stream?.let {
                yield(
                    Json.decodeFromStream<BangumiInfo>(it)
                )
            }
        }
}

@Preview
@Composable
fun BangumiDescriptionPreview(@PreviewParameter(BangumiPreviewProvider::class) bangumiInfo: BangumiInfo) {
    BangumiDescription(bangumiInfo)
}
