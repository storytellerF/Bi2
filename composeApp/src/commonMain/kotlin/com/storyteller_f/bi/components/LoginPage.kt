package com.storyteller_f.bi.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.storyteller_f.bi.AppNav
import com.storyteller_f.bi.LocalAppNav
import com.storyteller_f.bi.data.customViewModel
import com.storyteller_f.bi.network.*
import io.github.alexzhirkevich.qrose.QrCodePainter
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@Composable
fun LoginInternal(
    state: LoginState
) {
    val appNav = LocalAppNav.current
    val (qrcodeUrl, loadingState, checkState) = state
    val qrcodePainter = rememberQrCodePainter(
        data = qrcodeUrl ?: "not ready",
        shapes = QrShapes(
            darkPixel = QrPixelShape.roundCorners()
        )
    )
    Scaffold {
        Column {
            IconButton({
                appNav.gotoHome()
            }) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, "back to home")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                QrcodeCotent(qrcodePainter, loadingState, checkState, appNav)
            }
        }
    }
}

@Composable
private fun QrcodeCotent(
    qrcodePainter: QrCodePainter,
    loadingState: LoadingState?,
    checkState: LoadingState?,
    appNav: AppNav
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

@Composable
fun LoginPage() {
    val loginViewModel = customViewModel(QrcodeLoginViewModel::class)
    val loadingState by loginViewModel.state.collectAsState()
    val url by loginViewModel.qrcodeUrl.collectAsState()
    val checkState by loginViewModel.checkState.collectAsState()
    LoginInternal(LoginState(url, loadingState, checkState))
}
