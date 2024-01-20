package com.storyteller_f.bi.network

import androidx.paging.PagingSource
import bilibili.app.dynamic.v2.*
import bilibili.app.interfaces.v1.Cursor
import bilibili.app.interfaces.v1.CursorItem
import bilibili.app.interfaces.v1.CursorV2Req
import bilibili.app.interfaces.v1.GrpcHistoryClient
import bilibili.main.community.reply.v1.*
import com.storyteller_f.bi.entity.*
import com.storyteller_f.bi.entity.bangumi.BangumiInfo
import com.storyteller_f.bi.entity.search.SearchBangumiInfo
import com.storyteller_f.bi.entity.search.SearchListInfo
import com.storyteller_f.bi.entity.stream.PlayurlData
import io.github.aakira.napier.log

class CommentReplyCursor(val mode: Int, val next: Long, val prev: Long)

fun ReplyInfo.buildCommentInfo() = CommentInfo(
    count, dialog, member!!.face, id, like, content!!.message, member.name, parent, type
)


fun CursorItem.cover(): String {
    return card_ogv?.cover ?: card_ugc?.cover!!
}


fun CursorItem.type(): String {
    return when {
        card_ugc != null -> "ugc"
        card_ogv != null -> "ogc"
        card_article != null -> "article"
        card_live != null -> "live"
        else -> "cheese"
    }
}

fun CursorItem.progress(): Long {
    return when {
        card_ugc != null -> card_ugc.progress
        card_ogv != null -> card_ogv.progress
        card_article != null -> 0L
        card_live != null -> 0L
        else -> card_cheese?.progress!!
    }
}


class HistoryVideoItem(
    val business: String,
    val cover: String,
    val kid: Long,
    val oid: Long,
    val progress: Long,
    val title: String,
    val type: String,
)

data class MomentsDataInfo(
    val descMode: MomentsDesc?,
    val dynamicContent: DynamicContentInfo,
    val dynamicType: Int,
    val face: String,
    val labelText: String,
    val mid: String,
    val name: String,
    val stat: MomentsStat,
)

data class MomentsDesc(val desc: String?)

data class MomentsStat(val like: Long, val reply: Long, val repost: Long)

data class DynamicContentInfo(
    val id: String,
    val pic: String = "",
    val remark: String? = null,
    val title: String = "",
)

data class VideoDatum(
    val addAt: Int,
    val aid: Long,
    val bvid: String,
    val cid: Int,
    val copyright: Int,
    val count: Int,
    val createdTime: Int,
    val desc: String,
    val duration: Int,
    val dynamic: String,
    val name: String,
    val pic: String,
    val progress: Int,
    val pubDate: Int,
    val state: Int,
    /**
     * 分区id
     */
    val tid: Long,
    val title: String,
    val videos: Int,
)

class VideoDatumList(
    val count: Int,
    val list: List<VideoDatum>,
)

data class CommentInfo(
    val count: Long,
    val dialog: Long,
    val face: String,
    val id: Long,
    val like: Long,
    val message: String,
    val name: String,
    val parent: Long,
    val type: Long,
)

inline fun <T, D, R : Response<D>> T.serviceCatching(block: T.() -> R): Result<R> {
    return try {
        val value = block()
        if (value.isSuccess() && value.res != null) {
            Result.success(value)
        } else {
            Result.failure(value.error())
        }
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

fun <KEY : Any, DATUM : Any> Result<PagingData<KEY, DATUM>>.loadResult(): PagingSource.LoadResult<KEY, DATUM> {
    return fold(onSuccess = { (data, nextKey) ->
        PagingSource.LoadResult.Page(
            data = data,
            prevKey = null, // Only paging forward.
            nextKey = nextKey
        )
    }, onFailure = {
        PagingSource.LoadResult.Error(it)
    })
}

object Service {

    suspend fun bangumiInfo(seasonId: String): Result<ResultInfo2<BangumiInfo?>> = bangumiApi.serviceCatching {
        seasonInfo(seasonId)
    }

    suspend fun bangumiPlayUrlData(
        quality: Int, fnval: Int, epid: String, id: String
    ): Result<PlayurlData> {
        return playerApi.serviceCatching {
            getBangumiUrl(
                epid, id, quality.toString(), if (fnval > 2) "1" else null, fnval.toString()
            )
        }
    }

    suspend fun bangumiReport(realtimeProgress: String, aid: String, id: String, epid: String, sid: String) =
        videoApi.runCatching {
            reportBangumi(aid, id, epid, sid, realtimeProgress, realtimeProgress)
        }

    suspend fun bangumiResultInfo(aid: String, id: String, epid: String, sid: String) = playerApi.serviceCatching {
        getPlayerV2Info(aid = aid, cid = id, epId = epid, seasonId = sid)
    }

    suspend fun bangumiSearchResultInfo(
        keyword: String, loadSize: Int, pageNum: Int
    ): Result<ResultInfo<SearchListInfo<SearchBangumiInfo>>> = searchApi.serviceCatching {
        searchBangumi(
            keyword = keyword, pageNum = pageNum, pageSize = loadSize
        )
    }

    suspend fun commentReplyResult(
        oid: Long, pid: Long, key: CommentReplyCursor?
    ) = GrpcReplyClient(grpcClient).DetailList().runCatching {
        val req = DetailListReq(oid = oid, root = pid, type = 1, scene = DetailListScene.REPLY, cursor = key?.let {
            CursorReq(prev = it.prev, next = it.next, mode = Mode.fromValue(it.mode)!!)
        })
        val res = execute(req)
        val data = res.root?.replies.orEmpty().map {
            it.buildCommentInfo()
        }
        val nextKey = res.cursor?.takeIf { !it.isEnd }?.let {
            CommentReplyCursor(it.mode.value, it.next, it.prev)
        }
        PagingData(data, nextKey)
    }

    suspend fun commentResult(
        id: String, nextParam: Long?
    ) = GrpcReplyClient(grpcClient).MainList().runCatching {
        val req = MainListReq(id.toLong(), 1, CursorReq(next = nextParam ?: 0, mode = Mode.MAIN_LIST_HOT))

        val res = execute(req)
        val data = res.replies.map {
            it.buildCommentInfo()
        }
        val nextKey = res.cursor?.takeIf { !it.isEnd }?.next
        PagingData(data, nextKey)
    }

    suspend fun favoriteDetail(
        currentPage: Int,
        pageSize: Int,
        id: String,
    ) = userspaceApi.serviceCatching {
        mediaDetail(
            mediaId = id,
            pageNum = currentPage,
            pageSize = pageSize,
        )
    }

    suspend fun favoriteList(
        key: Int,
        mid: String,
        pageSize: Int,
    ) = userspaceApi.serviceCatching {
        favFolderList(
            mid, pageNum = key, pageSize = pageSize
        )
    }

    suspend fun historyResult(key: Pair<Long, Int>?) =
        GrpcHistoryClient(grpcClient).CursorV2().runCatching {

            val (lastMax, lastTp) = key ?: (0L to 0)
            log {
                /**
                 * 历史结果
                 * load: 0 0
                 * load: 1680969219 3
                 * load: 1680968482 3
                 * load: 1680963212 3
                 */
                /**
                 * 历史结果
                 * load: 0 0
                 * load: 1680969219 3
                 * load: 1680968482 3
                 * load: 1680963212 3
                 */
                "load: $lastMax $lastTp"
            }
            val req = CursorV2Req(cursor = Cursor(max = lastMax, maxTp = lastTp), business = "archive")
            val res = execute(req)
            log {
                "load: ${res.cursor}"
            }
            val data = res.items.map {
                HistoryVideoItem(
                    oid = it.oid,
                    kid = it.kid,
                    business = it.business,
                    progress = it.progress(),
                    cover = it.cover(),
                    title = it.title,
                    type = it.type()
                )
            }
            val nextKey = res.cursor?.takeIf { it.max != 0L }
            PagingData(data, if (nextKey == null) null else (nextKey.max) to nextKey.maxTp)
        }

    suspend fun momentRequest(key: Pair<String, String>?): Result<PagingData<Pair<String, String>, MomentsDataInfo>> =
        GrpcDynamicClient(grpcClient).DynVideo().runCatching {
            val (offset, baseline) = key ?: ("" to "")
            val type = if (offset.isBlank()) {
                Refresh.refresh_new
            } else {
                Refresh.refresh_history
            }
            val req = DynVideoReq(baseline, offset, refresh_type = type, local_time = 8)
            val result = execute(req)
            if (result.dynamic_list != null) {
                val dynamicListData = result.dynamic_list
                val itemsList = dynamicListData.list.filter { item ->
                    item.card_type != DynamicType.dyn_none && item.card_type != DynamicType.ad
                }.map { item ->
                    val modules = item.modules
                    val userModule = modules.first { it.module_author != null }.module_author!!
                    val descModule = modules.find { it.module_desc != null }?.module_desc!!
                    val dynamicModule = modules.first { it.module_dynamic != null }.module_dynamic!!
                    val statModule = modules.first { it.module_stat != null }.module_stat!!
                    MomentsDataInfo(
                        mid = userModule.author!!.mid.toString(),
                        name = userModule.author.name,
                        face = userModule.author.face,
                        labelText = userModule.ptime_label_text,
                        dynamicType = dynamicModule.type.value,
                        dynamicContent = dynamicModule.getDynamicContent(),
                        descMode = MomentsDesc(descModule.text),
                        stat = MomentsStat(statModule.reply, statModule.like, statModule.repost),
                    )
                }
                PagingData(itemsList, (dynamicListData.history_offset to dynamicListData.update_baseline))
            } else {
                PagingData(emptyList(), null)
            }
        }

    suspend fun playList() = userspaceApi.serviceCatching {
        toBePlay()
    }

    suspend fun requestQrcode() = authApi.qrCode()

    suspend fun requestQrcodeResult(authCode: String) = authApi.checkQrCode(authCode)


    suspend fun requestUserInfo(mid: Long) = userspaceApi.serviceCatching {
        space(mid.toString())
    }

    suspend fun searchUpper(
        keyword: String, loadSize: Int, pageNum: Int
    ) = searchApi.serviceCatching {
        searchUpper(
            keyword = keyword, pageNum = pageNum, pageSize = loadSize
        )
    }

    suspend fun searchVideo(
        keyword: String, pageNum: Int, pageSize: Int
    ) = searchApi.serviceCatching {
        searchArchive(
            keyword = keyword, order = "default", duration = 0, rid = 0, pageNum = pageNum, pageSize = pageSize
        )
    }

    suspend fun sessionInfo(seasonId: String) = bangumiBiliApi.serviceCatching {
        seasonSection(seasonId)
    }

    suspend fun userAccountInfo() = accountApi.account()

    suspend fun videoPlayerResultInfo(aid: String, id: String) = playerApi.serviceCatching {
        getPlayerV2Info(aid = aid, cid = id)
    }

    suspend fun videoPlayurlData(
        quality: Int, fnval: Int, aid: String, id: String
    ) = playerApi.serviceCatching {
        getVideoPalyUrl(
            aid, id, quality.toString(), if (fnval > 2) "1" else null, fnval.toString()
        )
    }

    suspend fun videoReport(realtimeProgress: String, aid: String, id: String) {
        videoApi.runCatching {
            reportVideo(aid, id, realtimeProgress, realtimeProgress)
        }
    }

    suspend fun videoResultInfo(type: String, videoId: String) =
        videoApi.serviceCatching {
            if (type == "AV") {
                info(videoId, null, "0", "32")
            } else {
                info(null, videoId, "0", "32")
            }
        }
}

private fun ModuleDynamic.getDynamicContent(): DynamicContentInfo {
    return when (type) {
        ModuleDynamicType.mdl_dyn_archive -> {
            val dynArchive = dyn_archive!!
            DynamicContentInfo(
                id = dynArchive.avid.toString(),
                title = dynArchive.title,
                pic = dynArchive.cover,
                remark = dynArchive.cover_left_text_2 + "    " + dynArchive.cover_left_text_3,
            )
        }

        ModuleDynamicType.mdl_dyn_pgc -> {
            val dynPgc = dyn_pgc!!
            DynamicContentInfo(
                id = dynPgc.season_id.toString(),
                title = dynPgc.title,
                pic = dynPgc.cover,
                remark = dynPgc.cover_left_text_2 + "    " + dynPgc.cover_left_text_3,
            )
        }

        else -> DynamicContentInfo("", "无法识别的稿件 $type")
    }
}