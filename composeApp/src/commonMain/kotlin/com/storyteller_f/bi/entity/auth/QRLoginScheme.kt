package com.storyteller_f.bi.entity.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QRLoginScheme(
    @SerialName("auth_code") val authCode: String,
    val url: String,
)