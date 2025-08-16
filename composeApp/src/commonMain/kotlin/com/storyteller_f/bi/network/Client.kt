package com.storyteller_f.bi.network

import com.storyteller_f.bi.apis.*
import com.storyteller_f.bi.fileSystem
import com.storyteller_f.bi.gs.LoginInfoState
import com.storyteller_f.bi.userAgent
import com.storyteller_f.bi.userPath
import de.jensklingenberg.ktorfit.ktorfit
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.core.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import org.kotlincrypto.hash.md.MD5
import java.nio.charset.Charset
import kotlin.time.ExperimentalTime

const val BUILD_VERSION = 1450000
const val BILI_APP_VERSION = "1.45.0"
const val MOBI_APP_HD = "android_hd" // 默认HD
const val STATISTICS_HD = """{"appId":5,"platform":3,"version":"$BILI_APP_VERSION","abtest":""}"""
const val PLATFORM = "android"
const val LOCALE = "zh_CN"
const val CHANNEL = "bili"

const val APP_KEY = "dfca71928277209b"
const val APP_SECRET = "b5475a8825547a4fc26c7d518eaaa02e"

const val REFERER = "https://www.bilibili.com/"
const val API_BASE = "https://api.bilibili.com/"
const val APP_BASE = "https://app.bilibili.com/"
const val GRPC_BASE = "https://grpc.biliapi.net/"
const val BANGUMI_BASE = "https://bangumi.bilibili.com"
const val PASSPORT_BASE = "https://passport.bilibili.com/"

val BiPlugin = createClientPlugin("CustomHeaderPlugin") {
    onRequest { request, _ ->
        if (request.method == HttpMethod.Get) {
            val old = request.url.parameters.build().toList()
            request.url.parameters.run {
                clear()
                commonParams(*old.toTypedArray()).forEach {
                    append(it.key, it.value)
                }
            }
        }
    }
    onResponse {
        if (it.status == HttpStatusCode.OK) {
            val text = it.bodyAsText()
            if (text.contains("\"code\":0")) {
                val path =
                    userPath(
                        "response${it.call.request.url.encodedPath}.${getMD5(it.call.request.url.encodedQuery)}.json"
                    ).toPath()
                if (!fileSystem.exists(path)) {
                    val parent = path.parent
                    if (parent == null || !fileSystem.exists(parent) && kotlin.runCatching {
                            fileSystem.createDirectories(parent)
                        }.getOrNull() == null
                    ) {
                        Napier.v {
                            "create $parent failed"
                        }
                    } else {
                        fileSystem.write(path, mustCreate = true) {
                            writeUtf8(text)
                        }
                    }
                }
            }
        }
    }
    transformRequestBody { request, content, _ ->
        if (request.method == HttpMethod.Post) {
            val old = when (content) {
                is FormDataContent -> content.formData.toList()
                else -> emptyList()
            }
            FormDataContent(
                parameters {
                    commonParams(*old.toTypedArray()).forEach {
                        append(it.key, it.value)
                    }
                }
            )
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

@OptIn(ExperimentalTime::class)
fun getTimeInterval(): Long {
    return kotlin.time.Clock.System.now().toEpochMilliseconds() / 1000
}

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
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    val fromEncoding = Charset.forName("UTF-8")
                    val toEncoding = Charset.forName("GBK")
                    Napier.v(tag = "HTTP Client", message = convertEncoding(message, fromEncoding, toEncoding))
                }
            }
            level = LogLevel.ALL
        }
        install(BiPlugin)
        expectSuccess = true
    }
}

fun convertEncoding(input: String, fromCharset: Charset, toCharset: Charset): String {
    // 将字符串从源编码转换为字节数组
    val bytes = input.toByteArray(fromCharset)
    // 使用目标编码构造新的字符串
    return String(bytes, toCharset)
}

private fun commonHeaders(): Map<String, String> {
    val headers = mutableMapOf(
        "user-agent" to userAgent,
        "referer" to REFERER,
        "env" to "prod",
        "app-key" to "android_hd",
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
        "platform" to PLATFORM,
        "channel" to CHANNEL,
        "mobi_app" to MOBI_APP_HD,
        "statistics" to STATISTICS_HD,
        "build" to BUILD_VERSION.toString(),
        "c_locale" to LOCALE,
        "s_locale" to LOCALE,
        "ts" to getTimeInterval().toString()
    )
    LoginInfoState.loginInfo?.tokenInfo?.let {
        parameters["access_key"] = it.accessToken
        parameters["mid"] = it.mid.toString()
    }
    val formUrlEncode = Parameters.build {
        parameters.keys.sorted().forEach { key ->
            val value = parameters[key]
            if (!value.isNullOrEmpty()) append(key, value)
        }
    }.formUrlEncode()
    parameters["sign"] = getMD5(formUrlEncode + APP_SECRET)
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
    ktorfit {
        httpClient(ktorClient).baseUrl(PASSPORT_BASE)
    }
}

val biliClient by lazy {
    ktorfit {
        httpClient(ktorClient).baseUrl(API_BASE)
    }
}

val bangumiClient by lazy {
    ktorfit {
        httpClient(ktorClient).baseUrl(BANGUMI_BASE)
    }
}

val appClient by lazy {
    ktorfit {
        httpClient(ktorClient).baseUrl(APP_BASE)
    }
}

val authApi by lazy {
    passportClient.createAuthApi()
}

val accountApi by lazy {
    appClient.createAccountApi()
}

val videoApi by lazy {
    appClient.createVideoAPI()
}

val userspaceApi by lazy {
    biliClient.createUserApi()
}

val searchApi by lazy {
    biliClient.createSearchApi()
}

val bangumiApi by lazy {
    bangumiClient.createBangumiAPI()
}

val bangumiBiliApi by lazy {
    biliClient.createBangumiBiliAPI()
}

val playerApi by lazy {
    biliClient.createPlayerAPI()
}
