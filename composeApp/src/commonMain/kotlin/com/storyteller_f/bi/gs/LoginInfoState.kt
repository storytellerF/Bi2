package com.storyteller_f.bi.gs

import com.storyteller_f.bi.entity.CookieInfo
import com.storyteller_f.bi.entity.LoginInfo
import com.storyteller_f.bi.fileSystem
import com.storyteller_f.bi.network.PlatformCookieManager
import com.storyteller_f.bi.userPath
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.cookies.*
import io.ktor.http.*
import io.ktor.util.date.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import kotlin.time.Duration.Companion.seconds

object LoginInfoState {
    private var _loginInfo: LoginInfo? = null
    val loginInfo: LoginInfo?
        get() = _loginInfo

    fun restore() {
        try {
            if (fileSystem.exists(path)) {
                val fromFile = fileSystem.read(path) {
                    readByteArray()
                }
                val jsonStr = fromFile.decodeToString()
                val loginInfo = Json.decodeFromString<LoginInfo>(jsonStr)
                _loginInfo = loginInfo
                Napier.i {
                    "restore login info in $path"
                }
            }
        } catch (e: Exception) {
            Napier.e(e) {
                "restore failed"
            }
        }
    }

    fun store(loginInfo: LoginInfo) {
        Napier.i {
            "save login info to $path"
        }
        _loginInfo = loginInfo
        val toFile = Json.encodeToString(loginInfo).toByteArray()
        fileSystem.write(path) {
            write(toFile)
        }
        loginInfo.cookieInfo?.let { saveCookie(it) }
    }

    fun delete() {
        Napier.i {
            "delete login info"
        }
        fileSystem.delete(path)
        PlatformCookieManager.close()
        _loginInfo = null
    }

    private val path get() = userPath("auth").toPath()

    private fun saveCookie(cookieInfo: CookieInfo) {
        runBlocking {
            cookieInfo.domains.forEach { domain ->
                cookieInfo.cookies.forEach { cookie ->
                    PlatformCookieManager.addCookie(
                        domain,
                        Cookie(
                            cookie.name,
                            cookie.value,
                            expires = GMTDate(cookie.expires.seconds.inWholeMilliseconds),
                            httpOnly = cookie.httpOnly == 1
                        )
                    )
                }
            }
        }
    }
}
