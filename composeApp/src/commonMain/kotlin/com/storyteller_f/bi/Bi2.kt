package com.storyteller_f.bi

import okio.FileSystem

expect val userAgent: String

expect val fileSystem: FileSystem

/**
 * 返回用户私有目录的文件
 */
expect fun userPath(id: String): String
