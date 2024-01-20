package com.storyteller_f.bi.entity.player

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubtitleJsonInfo(
    val Stroke: String,
    @SerialName("background_alpha") val backgroundAlpha: Float,
    @SerialName("background_color") val backgroundColor: String,
    val body: List<ItemInfo>,
    @SerialName("font_color") val fontColor: String,
    @SerialName("font_size") val fontSize: Float,
) {
    @Serializable
    data class ItemInfo(
        val from: Double,
        val to: Double,
        val location: Int,
        val content: String,
    )

}