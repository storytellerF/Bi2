package com.storyteller_f.bi

import net.harawata.appdirs.AppDirsFactory
import okio.FileSystem

actual val userAgent: String
    get() = ("Mozilla/5.0 (Linux; Android 11; SM-A202F Build/RP1A.200720.012; wv) " +
        "AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/112.0.5615.135 " +
        "Mobile Safari/537.36 1.39.0")

actual val fileSystem: FileSystem = FileSystem.SYSTEM

/**
 * 返回用户私有目录的文件
 */
actual fun userPath(id: String): String {
    val appDir: String = AppDirsFactory.getInstance().getUserDataDir("Bi2", "1.0", "storyteller_f")
    return "$appDir/$id.json"
}
