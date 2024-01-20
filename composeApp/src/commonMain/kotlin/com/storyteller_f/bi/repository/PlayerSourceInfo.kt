package com.storyteller_f.bi.repository

class PlayerSourceInfo(val lastPlayCid: String = "", val lastPlayTime: Long = 0L, val quality: Int = 0,
                       val acceptList: List<AcceptInfo> = emptyList(),
    val info2: VideoInfo2,
) {
    var url = info2.url
    var duration = info2.duration

    val description: String
        get() = acceptList.find { it.quality == quality }?.description ?: "未知清晰度"

    var height = info2.height
    var width = info2.width
    val screenProportion get() = width.toFloat() / height.toFloat() // 视频画面比例

    data class AcceptInfo(
        val quality: Int,
        val description: String,
    )
}