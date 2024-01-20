package com.storyteller_f.bi.components

import com.storyteller_f.bi.repository.BasePlayerRepository
import com.storyteller_f.bi.player.MediaSourceGroup

abstract class PlayerService(
    val size: VideoSize?,
    val initProgress: Long,
    val reportHistory: () -> Unit,
    val repository: BasePlayerRepository?,
    val source: MediaSourceGroup?,
)

class VideoSize(val width: Int, val height: Int)
