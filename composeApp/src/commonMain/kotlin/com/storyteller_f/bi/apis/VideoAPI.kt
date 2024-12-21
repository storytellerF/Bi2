package com.storyteller_f.bi.apis

import com.storyteller_f.bi.entity.ResultInfo
import com.storyteller_f.bi.entity.VideoInfo
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

interface VideoAPI {

    @GET("x/v2/view")
    suspend fun info(
        @Query("aid") aid: String?,
        @Query("bvid") bvid: String?,
        @Query("autoplay") autoplay: String,
        @Query("qn") qn: String
    ): ResultInfo<VideoInfo>

    @POST("x/v2/history/report")
    suspend fun reportVideo(
        @Query("aid") aid: String?,
        @Query("cid") cid: String?,
        @Query("progress") progress: String,
        @Query("realtime") realtime: String,
        @Query("type") type: String = "3"
    )

    @POST("x/v2/history/report")
    suspend fun reportBangumi(
        @Query("aid") aid: String?,
        @Query("cid") cid: String?,
        @Query("epid") epId: String,
        @Query("sid") sid: String,
        @Query("progress") progress: String,
        @Query("realtime") realtime: String,
        @Query("type") type: String = "4",
        @Query("sub_type") subType: String = "1"
    )
}
