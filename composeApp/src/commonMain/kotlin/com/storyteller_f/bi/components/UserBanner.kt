package com.storyteller_f.bi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.storyteller_f.bi.data.ViewModel
import com.storyteller_f.bi.data.viewModel
import com.storyteller_f.bi.entity.user.SpaceInfo
import com.storyteller_f.bi.entity.user.UserInfo
import com.storyteller_f.bi.gs.UserInfoState
import com.storyteller_f.bi.network.LoadingState
import com.storyteller_f.bi.network.Service.requestUserInfo
import com.storyteller_f.bi.network.error
import com.storyteller_f.bi.network.loaded
import com.storyteller_f.bi.network.loading
import com.storyteller_f.bi.ui.RemoteImage
import com.storyteller_f.bi.ui.StandBy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope

@Composable
fun UserBanner(u: UserInfo?, login: () -> Unit = {}) {
    val coverSize = Modifier
        .size(60.dp)
    val modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
    if (u != null) {
        val face = u.face
        Column {
            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                StandBy(coverSize) {
                    RemoteImage(
                        model = face, contentDescription = "avatar", modifier = coverSize
                    )
                }
                Text(
                    text = u.name,
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.size(8.dp))
                Dot(s = if (u.sex == 1) "M" else "F")
                Spacer(modifier = Modifier.size(8.dp))
                Dot(u.level.toString())
            }
            val v = viewModel(UserBannerViewModel::class)
            val info by v.data.collectAsState()
            Text(text = info?.card?.sign ?: "不说两句？", modifier = Modifier.padding(8.dp))
            Row(modifier = Modifier.padding(8.dp)) {
                Badge("follower ${u.follower}")
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Badge("following ${u.following}")
            }
        }
    } else {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = coverSize.background(Color.Blue))
            Button(modifier = Modifier.padding(start = 8.dp), onClick = {
                login()
            }) {
                Text(text = "login")
            }
        }
    }
}

@Composable
private fun Badge(text: String) {
    Text(
        text = text, modifier = Modifier
            .background(
                MaterialTheme.colorScheme.secondary,
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onSecondary
    )
}

@Composable
private fun Dot(s: String = "") {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(
                MaterialTheme.colorScheme.tertiary,
                RoundedCornerShape(16.dp)
            ), contentAlignment = Alignment.Center
    ) {
        Text(text = s, fontSize = 12.sp, color = MaterialTheme.colorScheme.onTertiary)
    }
}

class UserBannerViewModel : ViewModel() {
    val state = MutableStateFlow<LoadingState?>(null)
    val data = MutableStateFlow<SpaceInfo?>(null)

    init {
        load()
    }

    private fun load() {
        state.loading()
        val mid = UserInfoState.state.value?.mid
        if (mid == null || mid == 0L) {
            state.error(Exception("未登录"))
            return
        }
        viewModelScope.launch {
            requestUserInfo(mid).fold(onSuccess = {
                data.value = it.data
                state.loaded()
            }, onFailure = {
                state.error(it)
            })
        }
    }

}
