package com.storyteller_f.bi.apis

import com.storyteller_f.bi.entity.ListResponse
import com.storyteller_f.bi.entity.MediaDetailInfo
import com.storyteller_f.bi.entity.MediaListInfo
import com.storyteller_f.bi.entity.ResultInfo
import com.storyteller_f.bi.entity.SpaceInfo
import com.storyteller_f.bi.entity.VideoDatumList
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface UserApi {

    /**
     * 个人空间
     */
    @GET("x/v2/space")
    suspend fun space(@Query("vmid") id: String): ResultInfo<SpaceInfo>

    @GET("x/v3/fav/folder/created/list")
    suspend fun favFolderList(
        @Query("up_mid") upMid: String,
        @Query("pn") pageNum: Int,
        @Query("ps") pageSize: Int,
    ): ResultInfo<ListResponse<MediaListInfo>>

    /**
     * 收藏夹列表详情
     */
    @GET("x/v3/fav/resource/list")
    suspend fun mediaDetail(
        @Query("media_id") mediaId: String,
        @Query("pn") pageNum: Int,
        @Query("ps") pageSize: Int,
    ): ResultInfo<MediaDetailInfo>

    @GET("x/v2/history/toview")
    suspend fun playList(): ResultInfo<VideoDatumList>
}
