package com.storyteller_f.bi

import android.app.Application
import android.webkit.WebSettings
import com.storyteller_f.bi.gs.LoginInfoState
import com.storyteller_f.bi.gs.UserInfoState
import java.util.Locale

class BiApp : Application() {

    override fun onCreate() {
        super.onCreate()
        systemUserAgent = getSystemUserAgent()
        applicationFilesDir = filesDir.absolutePath

        LoginInfoState.restore()
        UserInfoState.restoreUserInfo()
    }

    private fun getSystemUserAgent(): String {
        val userAgent = try {
            WebSettings.getDefaultUserAgent(this)
        } catch (e: Exception) {
            System.getProperty("http.agent")
        }
        // 调整编码，防止中文出错
        val sb = StringBuffer()
        var i = 0
        val length = userAgent.length
        while (i < length) {
            val c = userAgent[i]
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format(Locale.getDefault(), "\\u%04x", c.code))
            } else {
                sb.append(c)
            }
            i++
        }
        return sb.toString()
    }
}
