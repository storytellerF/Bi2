package com.storyteller_f.bi.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.storyteller_f.bi.LOCALAppNav
import com.storyteller_f.bi.data.customViewModel
import com.storyteller_f.bi.gs.LoginInfoState
import com.storyteller_f.bi.gs.UserInfoState
import com.storyteller_f.bi.network.*
import com.storyteller_f.bi.network.Service.requestQrcode
import com.storyteller_f.bi.network.Service.requestQrcodeResult
import com.storyteller_f.bi.network.Service.userAccountInfo
import io.github.aakira.napier.Napier
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class QrcodeLoginViewModel : ViewModel() {
    val state = MutableStateFlow<LoadingState?>(null)
    val qrcodeUrl = MutableStateFlow<String?>(null)
    val checkState = MutableStateFlow<LoadingState?>(null)
    private var currentAuthCode: String? = null

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                state.value = LoadingState.Loading("load qrcode...")
                val res = requestQrcode()
                val data = res.data
                if (res.isSuccess && data!!.url.isNotEmpty() && data.authCode.isNotEmpty()) {
                    qrcodeUrl.value = data.url
                    currentAuthCode = data.authCode
                    state.loaded()
                    checkQr(data.authCode)
                } else {
                    state.error(Exception(res.message))
                }
            } catch (e: Throwable) {
                Napier.e(e) {
                    "request qrcode failed"
                }
                state.error(e)
            }
        }
    }

    private fun checkQr(authCode: String) {
        if (currentAuthCode != authCode) return
        checkState.loading("等待扫码")
        viewModelScope.launch {
            val res = requestQrcodeResult(authCode)
            when (res.code) {
                86039 -> {
                    checkState.loading("未确认")
                    // 未确认
                    delay(3000)
                    checkQr(authCode)
                }

                86090 -> {
                    checkState.loading("扫描成功，请点击确认")
                    // 已扫码未确认
                    delay(2000)
                    checkQr(authCode)
                }

                86038, -3 -> {
                    // 过期、失效
                    checkState.loading("二维码已过期，请刷新")
                }

                0 -> {
                    checkState.loading("扫码成功，正在读取信息")
                    // 成功
                    val loginInfo = res.data!!.toLoginInfo()
                    LoginInfoState.store(loginInfo)
                    getUserInfo()
                    checkState.loaded()
                }

                else -> {
                    // 发生错误
                    checkState.loading("登录失败，请稍后重试\n" + res.message)
                }
            }
        }
    }

    private suspend fun getUserInfo() {
        val res = userAccountInfo()
        checkState.value = if (res.isSuccess) {
            UserInfoState.saveUserInfo(res.data)
            LoadingState.Done(1)
        } else {
            LoadingState.Error(Exception(res.message))
        }
    }
}

@Composable
fun LoginInternal(
    state: LoginState
) {
    val (qrcodeUrl, loadingState, checkState) = state
    val qrcodePainter = rememberQrCodePainter(
        data = qrcodeUrl ?: "not ready",
        shapes = QrShapes(
            darkPixel = QrPixelShape.roundCorners()
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        val widthDp = 200
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = qrcodePainter,
                contentDescription = "test",
                modifier = Modifier
                    .size(
                        widthDp.dp
                    )
            )
            if (loadingState !is LoadingState.Done) {
                Text(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(8.dp)
                        .widthIn(max = widthDp.dp),
                    text = when (loadingState) {
                        null -> "waiting"
                        is LoadingState.Error -> loadingState.e.message.orEmpty()
                        is LoadingState.Loading -> loadingState.state
                        else -> "impossible"
                    }
                )
            }
        }
        val appNav = LOCALAppNav.current
        if (checkState != null) {
            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(8.dp)
                    .clickable {
                        if (checkState is LoadingState.Done) {
                            appNav.gotoHome()
                        }
                    },
                text = when (checkState) {
                    is LoadingState.Done -> "扫码成功，点击返回"
                    is LoadingState.Loading -> checkState.state
                    is LoadingState.Error -> checkState.e.message.toString()
                }
            )
        }
    }
}

@Composable
fun LoginPage() {
    val loginViewModel = customViewModel(QrcodeLoginViewModel::class)
    val loadingState by loginViewModel.state.collectAsState()
    val url by loginViewModel.qrcodeUrl.collectAsState()
    val checkState by loginViewModel.checkState.collectAsState()
    LoginInternal(LoginState(url, loadingState, checkState))
}
