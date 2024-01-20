package com.storyteller_f.bi.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginInfo(
    @SerialName("cookie_info") val cookieInfo: CookieInfo?,
    val sso: List<String>?,
    @SerialName("token_info") val tokenInfo: TokenInfo,
)

@Serializable
data class QrLoginInfo(
    @SerialName("access_token") val accessToken: String,
    @SerialName("cookie_info") val cookieInfo: CookieInfo?,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("is_new") val isNew: Boolean,
    val mid: Long,
    @SerialName("refresh_token") val refreshToken: String,
    val sso: List<String>?,
    @SerialName("token_info") val tokenInfo: TokenInfo,
) {
    fun toLoginInfo() = LoginInfo(
        cookieInfo,
        sso,
        tokenInfo
    )
}

@Serializable
data class CookieInfo(
    val cookies: List<Cookie>,
    val domains: List<String>,
)

@Serializable
data class Cookie(
    val expires: Int,
    @SerialName("http_only") val httpOnly: Int,
    val name: String,
    val value: String,
) {
    fun getValue(domain: String): String {
        return "$name=$value;Expires=$expires;Domain=$domain;${if (httpOnly == 1) "HTTPOnly;" else ""}"
    }
}

@Serializable
data class TokenInfo(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    val mid: Long,
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class QRLoginScheme(
    @SerialName("auth_code") val authCode: String,
    val url: String,
)
