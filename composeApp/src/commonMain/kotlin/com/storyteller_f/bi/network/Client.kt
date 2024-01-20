package com.storyteller_f.bi.network

import com.storyteller_f.bi.gs.LoginInfoState
import com.storyteller_f.bi.gs.getOrCreateBuvidId
import com.storyteller_f.bi.userAgent
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.core.*
import kotlinx.datetime.Clock
import org.kotlincrypto.hash.md.MD5

const val BUILD_VERSION = 1390002
const val BILI_APP_VERSION = "1.39.0"

const val APP_KEY = "dfca71928277209b"
const val APP_SECRET = "b5475a8825547a4fc26c7d518eaaa02e"

const val REFERER = "https://www.bilibili.com/"
const val APP_BASE = "https://app.bilibili.com/"
const val GRPC_BASE = "https://grpc.biliapi.net/"

val BiPlugin = createClientPlugin("CustomHeaderPlugin") {
    onRequest { request, _ ->
        if (request.method == HttpMethod.Get) {
            val old = request.url.parameters.build().toList()
            request.url.parameters.run {
                commonParams(*old.toTypedArray()).forEach {
                    append(it.key, it.value)
                }
            }
        }
    }
    transformRequestBody { request, content, _ ->
        if (request.method == HttpMethod.Post && content is FormDataContent) {
            val old = content.formData.toList()
            FormDataContent(parameters {
                commonParams(*old.toTypedArray()).forEach {
                    append(it.key, it.value)
                }
            })
        } else {
            null
        }
    }
}

private fun Parameters.toList(): List<Pair<String, String>> {
    val old = entries().map {
        it.key to it.value.first()
    }
    return old
}

fun getTimeInterval(): Long = Clock.System.now().toEpochMilliseconds()

@OptIn(ExperimentalStdlibApi::class)
fun getMD5(info: String): String {
    val md5 = MD5()
    md5.update(info.toByteArray())
    val encryption = md5.digest()
    val format = HexFormat {
        number {
            removeLeadingZeros = true
        }
    }
    return encryption.map {
        0xff and it.toInt()
    }.joinToString("") {
        val toHexString = it.toHexString(format)
        if (toHexString.length == 1) {
            "0$toHexString"
        } else {
            toHexString
        }
    }
}

val ktorClient by lazy {
    HttpClient {
        defaultRequest {
            headers {
                commonHeaders().forEach {
                    append(it.key, it.value)
                }
            }
        }
        install(HttpCookies) {
            storage = PlatformCookieManager
        }
        install(ContentNegotiation) {
            json()
        }
        install(Logging) {
            level = LogLevel.ALL
        }
        install(BiPlugin)
    }
}

private fun commonHeaders(): Map<String, String> {
    val headers = mutableMapOf(
        "user-agent" to userAgent,
        "referer" to REFERER,
        "env" to "prod",
        "app-key" to "android",
        "x-bili-aurora-eid" to "UlMFQVcABlAH",
        "x-bili-aurora-zone" to "sh001",
    )
    LoginInfoState.loginInfo?.tokenInfo?.let {
        headers["x-bili-mid"] = it.mid.toString()
    }
    return headers
}

fun commonParams(vararg param: Pair<String, String>): Map<String, String> {
    val parameters = mutableMapOf(
        *param,
        "appkey" to APP_KEY,
        "build" to BUILD_VERSION.toString(),
        "buvid" to getOrCreateBuvidId(),
        "mobi_app" to "android",
        "platform" to "android",
        "ts" to getTimeInterval().toString()
    )
    LoginInfoState.loginInfo?.tokenInfo?.let {
        parameters["access_key"] = it.accessToken
        parameters["mid"] = it.mid.toString()
    }
    parameters["sign"] = getMD5(Parameters.build {
        parameters.keys.sorted().forEach { key ->
            val value = parameters[key]
            if (!value.isNullOrEmpty()) append(key, value)
        }
    }.formUrlEncode() + APP_SECRET)

    return parameters
}

object PlatformCookieManager : CookiesStorage {
    private val instance = mutableMapOf<Url, MutableList<Cookie>>()
    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        instance.getOrPut(requestUrl) {
            mutableListOf()
        }.add(cookie)
    }

    override fun close() {
        instance.clear()
    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        return instance[requestUrl].orEmpty()
    }

}

val passportClient by lazy {
    Ktorfit.Builder().httpClient(ktorClient).baseUrl("https://passport.bilibili.com/").build()
}

val biliClient by lazy {
    Ktorfit.Builder().httpClient(ktorClient).baseUrl(APP_BASE).build()
}

val bangumiClient by lazy {
    Ktorfit.Builder().httpClient(ktorClient).baseUrl("https://bangumi.bilibili.com").build()
}

val authApi by lazy {
    passportClient.create<com.storyteller_f.bi.apis.AuthApi>()
}

val accountApi by lazy {
    biliClient.create<com.storyteller_f.bi.apis.AccountApi>()
}

val videoApi by lazy {
    biliClient.create<com.storyteller_f.bi.apis.VideoAPI>()
}

val userspaceApi by lazy {
    biliClient.create<com.storyteller_f.bi.apis.UserApi>()
}

val searchApi by lazy {
    biliClient.create<com.storyteller_f.bi.apis.SearchApi>()
}

val bangumiApi by lazy {
    bangumiClient.create<com.storyteller_f.bi.apis.BangumiAPI>()
}

val bangumiBiliApi by lazy {
    biliClient.create<com.storyteller_f.bi.apis.BangumiBiliAPI>()
}

val playerApi by lazy {
    biliClient.create<com.storyteller_f.bi.apis.PlayerAPI>()
}
