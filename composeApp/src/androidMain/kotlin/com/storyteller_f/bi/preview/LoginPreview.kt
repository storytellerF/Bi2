package com.storyteller_f.bi.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.storyteller_f.bi.components.LoginInternal
import com.storyteller_f.bi.network.LoadingState
import com.storyteller_f.bi.network.LoginState

@Preview
@Composable
fun LoginPreview(
    @PreviewParameter(LoginPreviewProvider::class) state: LoginState
) {
    LoginInternal(state)
}

class LoginPreviewProvider : PreviewParameterProvider<LoginState> {
    override val values: Sequence<LoginState>
        get() = sequence {
            yield(LoginState("hello", LoadingState.Done, LoadingState.Done))
            yield(LoginState(null, LoadingState.Loading("loading"), null))
        }
}
