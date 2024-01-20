package com.storyteller_f.bi.entity.bangumi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeasonSectionInfo(
    @SerialName("main_section") val mainSection: SectionInfo?,
    val section: List<SectionInfo>,
) {
    @Serializable
    data class SectionInfo(
        val episodes: List<EpisodeInfo>,
        val id: String,
        val title: String,
        val type: Int,
    )

}