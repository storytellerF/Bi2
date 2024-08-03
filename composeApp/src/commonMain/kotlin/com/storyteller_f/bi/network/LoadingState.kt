package com.storyteller_f.bi.network

import com.storyteller_f.bi.entity.Response
import com.storyteller_f.bi.entity.ResultInfo2
import kotlinx.coroutines.flow.MutableStateFlow

sealed class LoadingState {
    class Loading(val state: String) : LoadingState()
    class Error(val e: Throwable) : LoadingState()
    class Done(val itemCount: Int = 1) : LoadingState()
}

class LoadingHandler<T>(val refresh: suspend () -> Unit) {
    val state: MutableStateFlow<LoadingState?> = MutableStateFlow(null)
    val data: MutableStateFlow<T?> = MutableStateFlow(null)
}


fun MutableStateFlow<LoadingState?>.loaded() {
    value = LoadingState.Done()
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
    service: () -> Result<ResultInfo2<T?>>,
) {
    val state = handler.state
    val data = handler.data
    state.loading()
    service().onSuccess { res ->
        val result = res.result
        if (res.isSuccess() && result != null) {
            data.value = result
            state.loaded()
        } else state.error(res.error())
    }.onFailure {
        state.error(it)
    }
}


fun<T> Response<T>.error(): Exception {
    return Exception("$code: $message")
}

data class LoginState(
    val qrcodeUrl: String?,
    val loadingState: LoadingState?,
    val checkState: LoadingState?
)
