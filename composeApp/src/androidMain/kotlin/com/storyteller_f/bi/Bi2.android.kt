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

actual val userAgent: String
    get() = """
            |$systemUserAgent 
            |${BILI_APP_VERSION} os/android model/${Build.MODEL} mobi_app/android_hd 
            |build/${BUILD_VERSION} channel/bili innerVer/${BUILD_VERSION} 
            |osVer/${Build.VERSION.RELEASE} network/2
    """.trimMargin().replace("\n", "")
actual val fileSystem: FileSystem = FileSystem.SYSTEM
