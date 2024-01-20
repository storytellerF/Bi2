package com.storyteller_f.bi.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.storyteller_f.bi.gs.LoginInfoState
import com.storyteller_f.bi.gs.UserInfoState
import com.storyteller_f.bi.network.LoadingState
import com.storyteller_f.bi.network.Service.requestQrcode
import com.storyteller_f.bi.network.Service.requestQrcodeResult
import com.storyteller_f.bi.network.Service.userAccountInfo
import com.storyteller_f.bi.network.error
import com.storyteller_f.bi.network.loaded
import com.storyteller_f.bi.network.loading
import io.github.aakira.napier.Napier
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
                    val data = res.data
                    if (data != null) {
                        val loginInfo = data.toLoginInfo()
                        LoginInfoState.store(loginInfo)
                        try {
                            val res1 = userAccountInfo()
                            checkState.value = if (res1.isSuccess) {
                                UserInfoState.saveUserInfo(res1.data)
                                LoadingState.Done
                            } else {
                                LoadingState.Error(Exception(res1.message))
                            }
                        } catch (e: Exception) {
                            checkState.loaded()
                        }
                    } else {
                        checkState.error("login info is null")
                    }
                }

                else -> {
                    // 发生错误
                    checkState.loading("登录失败，请稍后重试\n" + res.message)
                }
            }
        }
    }
}
