package com.storyteller_f.bi.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.storyteller_f.bi.components.UserBanner
import com.storyteller_f.bi.entity.UserInfo

class UserBannerPreviewProvider : PreviewParameterProvider<UserInfo?> {
    override val values: Sequence<UserInfo?>
        get() = sequence {
            yield(null)
            yield(UserInfo(0, "storyteller f", "", "", 50.0, 50.0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1))
        }
}

@Preview
@Composable
fun UserBannerPreview(@PreviewParameter(UserBannerPreviewProvider::class) userInfo: UserInfo) {
    UserBanner(userInfo)
}
