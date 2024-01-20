package com.storyteller_f.bi

import okio.FileSystem
import platform.Foundation.NSHomeDirectory

actual val userAgent: String = ""

actual val fileSystem: FileSystem = FileSystem.SYSTEM

/**
 * 返回用户私有目录的文件
 */
actual fun realPath(id: String): String {
    return "${NSHomeDirectory()}/$id.json"
}
