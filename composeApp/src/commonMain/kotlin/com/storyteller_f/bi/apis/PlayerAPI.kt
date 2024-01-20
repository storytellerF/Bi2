package com.storyteller_f.bi.apis

import com.storyteller_f.bi.entity.BangumiData
import com.storyteller_f.bi.entity.PlayerV2Info
import com.storyteller_f.bi.entity.ResultInfo
import com.storyteller_f.bi.entity.VideoData
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Query

interface PlayerAPI {

    @GET("x/player/v2")
    suspend fun getPlayerV2Info(
        @Query("aid") aid: String,
        @Query("cid") cid: String,
    ): ResultInfo<PlayerV2Info>

    @GET("x/player/v2")
    suspend fun getPlayerV2Info(
        @Query("aid") aid: String,
        @Query("cid") cid: String,
        @Query("ep_id") epId: String,
        @Query("season_id") seasonId: String,
    ): ResultInfo<PlayerV2Info>

    @GET("x/player/playurl")
    suspend fun getVideoPalyUrl(
        @Query("avid") avid: String,
        @Query("cid") cid: String,
        @Query("qn") quality: String,
        @Query("fourk") fourk: String?,
        @Query("fnval") fnval: String,
        @Query("fnver") fnver: String = "0",
        @Query("force_host") forceHost: String = "2",
        @Query("type") type: String = "",
        @Query("otype") otype: String = "json",
        @Header("Referer") referer: String = "https://www.bilibili.com/av$avid",
        @Header(
            "User-Agent"
        ) userAgent: String = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/63.0.3239.84 Safari/537.36"
    ): ResultInfo<VideoData>

    @GET("pgc/player/api/playurl")
    suspend fun getBangumiUrl(
        @Query("ep_id") epid: String,
        @Query("cid") cid: String,
        @Query("qn") qn: String,
        @Query("fourk") fourk: String?,
        @Query("fnval") fnval: String,
        @Query("fnver") fnver: String = "0",
        @Query("force_host") forceHost: String = "2",
        @Query("season_type") seasonType: String = "1",
        @Query("module") module: String = "bangumi",
        @Query("track_path") trackPath: String = "",
        @Query("device") device: String = "android",
        @Query("mobi_app") app: String = "android",
        @Query("platform") platform: String = "android",
    ): BangumiData
}
