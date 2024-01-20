package com.storyteller_f.bi.apis

import com.storyteller_f.bi.entity.BangumiInfo
import com.storyteller_f.bi.entity.ResultInfo
import com.storyteller_f.bi.entity.SeasonSectionInfo
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface BangumiAPI {

    /**
     * 番剧信息
     */
    @GET("view/api/season")
    suspend fun seasonInfo(@Query("season_id") seasonId: String): ResultInfo<BangumiInfo?>
}

interface BangumiBiliAPI {
    /**
     * 番剧剧集信息
     */
    @GET("pgc/web/season/section")
    suspend fun seasonSection(@Query("season_id") seasonId: String): ResultInfo<SeasonSectionInfo?>
}
