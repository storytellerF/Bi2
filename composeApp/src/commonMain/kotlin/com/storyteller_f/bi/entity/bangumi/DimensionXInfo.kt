package com.storyteller_f.bi.entity.bangumi

import kotlinx.serialization.Serializable

@Serializable
data class DimensionXInfo(
    val height: Int,
    val rotate: Int,
    val width: Int,
)