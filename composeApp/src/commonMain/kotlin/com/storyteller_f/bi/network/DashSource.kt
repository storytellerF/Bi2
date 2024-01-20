package com.storyteller_f.bi.network

import com.storyteller_f.bi.entity.Dash
import com.storyteller_f.bi.entity.DashItem

class DashSource(
    private val quality: Int,
    private val dashData: Dash
) {

    fun getDashVideo(): DashItem? {
        val videoList = dashData.video
        val conditionStreams = videoList.find { it.id == quality }
        return when {
            conditionStreams != null -> conditionStreams
            videoList.isNotEmpty() -> videoList[videoList.size - 1]
            else -> null
        }
    }

    private fun getDashAudio(): DashItem? {
        val audioList = dashData.audio
        if (audioList?.isNotEmpty() == true) {
            return audioList[0]
        }
        return null
    }

    fun getMDPUrl(
        video: DashItem = getDashVideo()!!
    ): String {
        val audio = getDashAudio()
        val mpdStr = """
<MPD xmlns="urn:mpeg:DASH:schema:MPD:2011" profiles="urn:mpeg:dash:profile:isoff-on-demand:2011" type="static" mediaPresentationDuration="PT${dashData.duration}S" minBufferTime="PT${dashData.minBufferTime}S">
    <Period start="PT0S">
        <AdaptationSet>
            <ContentComponent contentType="video" id="1" />
            <Representation bandwidth="${video.bandwidth}" codecs="${video.codecs}" height="${video.height}" id="${video.id}" mimeType="${video.mimeType}" width="${video.width}">
                <BaseURL></BaseURL>
                <SegmentBase indexRange="${video.segmentBase.indexRange}">
                    <Initialization range="${video.segmentBase.initialization}" />
                </SegmentBase>
            </Representation>
        </AdaptationSet>
        ${
            if (audio != null) {
                val audioUrl = audio.baseUrl
                """
                 <AdaptationSet>
                    <ContentComponent contentType="audio" id="2" />
                    <Representation bandwidth="${audio.bandwidth}" codecs="${audio.codecs}" id="${audio.id}" mimeType="${audio.mimeType}" >
                        <BaseURL>${audioUrl.replace("&", "&amp;")}</BaseURL>
                        <SegmentBase indexRange="${audio.segmentBase.indexRange}">
                            <Initialization range="${audio.segmentBase.initialization}" />
                        </SegmentBase>
                    </Representation>
                </AdaptationSet>
                """.trimIndent()
            } else {
                ""
            }
        }
    </Period>
</MPD>
        """.trimIndent()
        val url = video.baseUrl
        return "[dash-mpd]\n" + url + "\n" + mpdStr.replace("\n", "")
    }
}
