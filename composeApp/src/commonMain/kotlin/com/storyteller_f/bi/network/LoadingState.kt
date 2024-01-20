package com.storyteller_f.bi.network

import com.storyteller_f.bi.entity.ResultInfo
import kotlinx.coroutines.flow.MutableStateFlow

sealed class LoadingState {
    data class Loading(val state: String) : LoadingState()
    data class Error(val e: Throwable) : LoadingState()
    data object Done : LoadingState()
}

class LoadingHandler<T>(val refresh: suspend () -> Unit) {
    val state: MutableStateFlow<LoadingState?> = MutableStateFlow(null)
    val data: MutableStateFlow<T?> = MutableStateFlow(null)
}

fun MutableStateFlow<LoadingState?>.loaded() {
    value = LoadingState.Done
}

fun MutableStateFlow<LoadingState?>.error(e: Throwable) {
    value = LoadingState.Error(e)
}

fun MutableStateFlow<LoadingState?>.error(e: String) {
    value = LoadingState.Error(Exception(e))
}

fun MutableStateFlow<LoadingState?>.loading(message: String = "") {
    value = LoadingState.Loading(message)
}

inline fun <T> request(
    handler: LoadingHandler<T>,
    service: () -> Result<ResultInfo<T?>>,
) {
    val state = handler.state
    val data = handler.data
    state.loading()
    service().onSuccess { res ->
        val result = res.result
        if (res.code == 0 && result != null) {
            data.value = result
            state.loaded()
        } else {
            state.error(res.error())
        }
    }.onFailure {
        state.error(it)
    }
}

fun<T> ResultInfo<T>.error(): Exception {
    return Exception("$code: $message")
}

data class LoginState(
    val qrcodeUrl: String?,
    val loadingState: LoadingState?,
    val checkState: LoadingState?
)
