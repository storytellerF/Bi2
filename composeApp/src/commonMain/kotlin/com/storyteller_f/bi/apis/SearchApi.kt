package com.storyteller_f.bi.apis

import com.storyteller_f.bi.entity.*
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface SearchApi {

    /**
     * 综合
     */
    @GET(
        "x/v2/search?actionKey=appkey&" +
            "appkey=27eb53fc9058f8c3&" +
            "build=3710&" +
            "device=phone&" +
            "mobi_app=iphone&" +
            "platform=ios"
    )
    suspend fun searchArchive(
        @Query("keyword") keyword: String,
        @Query("pn") pageNum: Int,
        @Query("ps") pageSize: Int,
        @Query("order") order: String,
        @Query("duration") duration: Int,
        @Query("rid") rid: Int,
    ): ResultInfo<SearchResultInfo<SearchArchiveInfo>>

    /**
     * 番剧
     */
    @GET(
        "x/v2/search/type?actionKey=appkey&" +
            "appkey=27eb53fc9058f8c3&" +
            "build=3710&" +
            "device=phone&" +
            "mobi_app=iphone&" +
            "platform=ios&" +
            "type=1"
    )
    suspend fun searchBangumi(
        @Query("keyword") keyword: String,
        @Query("pn") pageNum: Int,
        @Query("ps") pageSize: Int
    ): ResultInfo<SearchListInfo<SearchBangumiInfo>>

    /**
     * UP主
     */
    @GET(
        "x/v2/search/type?actionKey=appkey&" +
            "appkey=27eb53fc9058f8c3&" +
            "build=3710&" +
            "device=phone&" +
            "mobi_app=iphone&" +
            "platform=ios&" +
            "type=2"
    )
    suspend fun searchUpper(
        @Query("keyword") keyword: String,
        @Query("pn") pageNum: Int,
        @Query("ps") pageSize: Int
    ): ResultInfo<SearchListInfo<SearchUpperInfo>>
}
