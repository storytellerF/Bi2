package com.storyteller_f.bi.gs

import com.storyteller_f.bi.entity.UserInfo
import com.storyteller_f.bi.fileSystem
import com.storyteller_f.bi.userPath
import io.github.aakira.napier.Napier
import io.github.aakira.napier.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath

object UserInfoState {

    var state = MutableStateFlow<UserInfo?>(null)

    fun saveUserInfo(userInfo: UserInfo?) {
        state.value = userInfo
        val file = userPath("user.data").toPath()
        if (userInfo != null) {
            val jsonStr = Json.encodeToString<UserInfo>(userInfo)
            fileSystem.write(file) {
                writeUtf8(jsonStr)
            }
            Napier.i {
                "save user info in $file"
            }
        } else {
            fileSystem.delete(file)
        }
    }

    fun logout() {
        LoginInfoState.delete()
        state.value = null
        saveUserInfo(null)
    }

    fun restoreUserInfo() {
        try {
            val file = userPath("user.data").toPath()
            if (fileSystem.exists(file)) {
                val jsonStr = fileSystem.read(file) {
                    readUtf8()
                }
                val localInfo = Json.decodeFromString<UserInfo>(jsonStr)
                state.value = localInfo
                Napier.i {
                    "restore user info in $file"
                }
            }
        } catch (e: Throwable) {
            log(throwable = e) {
                "readUserInfo : "
            }
        }
    }
}
