package com.storyteller_f.bi

import android.os.Build
import com.storyteller_f.bi.network.BILI_APP_VERSION
import com.storyteller_f.bi.network.BUILD_VERSION
import okio.FileSystem

lateinit var applicationFilesDir: String
lateinit var systemUserAgent: String

actual fun userPath(id: String): String {
    return "$applicationFilesDir/$id"
}

/**
 * Mozilla/5.0 (Linux; Android 11; SM-A202F Build/RP1A.200720.012; wv)
 * AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/112.0.5615.135 Mobile Safari/537.36 1.39.0
 * os/android model/SM-A202F mobi_app/android_hd build/1390002 channel/bili innerVer/1390002osVer/11 network/2
 */
actual val userAgent: String
    get() = """
            |$systemUserAgent 
            |${BILI_APP_VERSION} os/android model/${Build.MODEL} mobi_app/android_hd 
            |build/${BUILD_VERSION} channel/bili innerVer/${BUILD_VERSION} 
            |osVer/${Build.VERSION.RELEASE} network/2
    """.trimMargin().replace("\n", "")
actual val fileSystem: FileSystem
    get() = FileSystem.SYSTEM
