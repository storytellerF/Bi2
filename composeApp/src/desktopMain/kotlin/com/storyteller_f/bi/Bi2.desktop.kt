package com.storyteller_f.bi

import net.harawata.appdirs.AppDirsFactory
import okio.FileSystem

actual val userAgent: String = ""

actual val fileSystem: FileSystem = FileSystem.SYSTEM

/**
 * 返回用户私有目录的文件
 */
actual fun userPath(id: String): String {
    val appDir: String = AppDirsFactory.getInstance().getUserDataDir("Bi2", "1.0", "storyteller_f")
    return "$appDir/$id.json"
}
