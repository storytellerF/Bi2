package com.storyteller_f.bi.gs

import com.benasher44.uuid.uuid4
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

private var vidCache: String = ""
private val settings = Settings()

fun getOrCreateBuvidId(): String {
    val v = vidCache
    if (v.isNotBlank()) return v
    var saved = settings.getString("buvid", "")
    if (saved.isBlank()) {
        saved = generateBuvid().apply {
            settings["buvid"] = this
        }
    }
    vidCache = saved
    return saved
}

/**
 * 获得一个UUID
 * @return String UUID
 */
private fun getUUID() = uuid4().toString().replace("-", "")

fun generateBuvid(): String {
    val uuid = getUUID() + getUUID()
    return "XY" + uuid.substring(0, 35).uppercase()
}
