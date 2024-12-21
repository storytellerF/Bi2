package com.storyteller_f.bi.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.storyteller_f.bi.components.PlayerService
import com.storyteller_f.bi.repository.BasePlayerRepository

@Composable
expect fun rememberPlayerService(videoPlayerRepository: BasePlayerRepository?, initProgress: Long): State<PlayerService>

@Composable
expect fun VideoView(
    service: PlayerService,
    aspectRatio: Boolean,
    switchFullscreenMode: ((Boolean) -> Unit)?
)
