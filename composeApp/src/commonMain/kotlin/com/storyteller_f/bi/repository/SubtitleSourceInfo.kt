package com.storyteller_f.bi.repository

data class SubtitleSourceInfo(
    val id: String,
    val lan: String,
    val lan_doc: String,
    val subtitle_url: String,
    val ai_status: Int,
)
