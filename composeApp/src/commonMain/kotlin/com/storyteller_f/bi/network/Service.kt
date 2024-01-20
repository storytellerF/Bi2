package com.storyteller_f.bi.network

import androidx.paging.PagingSource
import bilibili.app.dynamic.v2.*
import bilibili.app.interfaces.v1.Cursor
import bilibili.app.interfaces.v1.CursorItem
import bilibili.app.interfaces.v1.CursorV2Req
import bilibili.app.interfaces.v1.GrpcHistoryClient
import bilibili.main.community.reply.v1.*
import com.squareup.wire.GrpcCall
import com.storyteller_f.bi.entity.*
import com.storyteller_f.bi.entity.BangumiInfo
import com.storyteller_f.bi.entity.SearchBangumiInfo
import com.storyteller_f.bi.entity.SearchListInfo
import com.storyteller_f.bi.fileSystem
import com.storyteller_f.bi.userPath
import io.github.aakira.napier.log
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import okio.Path.Companion.toPath
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

suspend inline fun <S : Any, reified R : Any> GrpcCall<S, R>.executeAndSave(s: S): R {
    val result = execute(s)
    val path = userPath("${method.path}/${s.hashCode()}.json").toPath()
    if (!fileSystem.exists(path)) {
        val parent = path.parent
        if (parent != null && (
                fileSystem.exists(parent) || kotlin.runCatching { fileSystem.createDirectories(parent) }
                    .getOrNull() != null
                )
        ) {
            fileSystem.write(path, mustCreate = true) {
                writeUtf8(objectToJsonObject(result, R::class).toString())
            }
        }
    }
    return result
}

// 通过反射将对象转换为 JsonObject
inline fun <reified T : Any> objectToJsonObject(obj: T, kclazz: KClass<T>): JsonObject {
    val jsonBuilder = JsonObjectBuilder()

    // 获取类的所有属性
    val properties = kclazz.declaredMemberProperties

    // 通过反射获取每个属性的名称和值
    properties.forEach { property ->
        val name = property.name
        // 将值转换为 JsonElement
        val jsonValue = when (val value = property.get(obj)) {
            is String -> JsonPrimitive(value)
            is Int -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Double -> JsonPrimitive(value)
            else -> JsonNull // 如果是其他类型，使用 JsonNull
        }

        jsonBuilder.put(name, jsonValue)
    }

    // 返回构建的 JsonObject
    return jsonBuilder.build()
}

// 自定义 JsonObjectBuilder 来构建 JsonObject
class JsonObjectBuilder {
    private val map = mutableMapOf<String, JsonElement>()

    fun put(name: String, value: JsonElement) {
        map[name] = value
    }

    fun build(): JsonObject {
        return JsonObject(map)
    }
}

class CommentReplyCursor(val mode: Int, val next: Long, val prev: Long)

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

inline fun <T, D> T.serviceCatching(block: T.() -> ResultInfo<D>): Result<ResultInfo<D>> {
    return try {
        val value = block()
        if (value.code == 0 && value.res != null) {
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

    suspend fun bangumiInfo(seasonId: String): Result<ResultInfo<BangumiInfo?>> = bangumiApi.serviceCatching {
        seasonInfo(seasonId)
    }

    suspend fun bangumiPlayUrlData(
        quality: Int,
        fnval: Int,
        epid: String,
        id: String
    ): Result<BangumiData> {
        return kotlin.runCatching {
            val bangumiUrl = playerApi.getBangumiUrl(
                epid,
                id,
                quality.toString(),
                if (fnval > 2) "1" else null,
                fnval.toString()
            )
            if (bangumiUrl.code == 0) {
                bangumiUrl
            } else {
                throw Exception("${bangumiUrl.code} ${bangumiUrl.message}")
            }
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
        keyword: String,
        loadSize: Int,
        pageNum: Int
    ): Result<ResultInfo<SearchListInfo<SearchBangumiInfo>>> = searchApi.serviceCatching {
        searchBangumi(
            keyword = keyword,
            pageNum = pageNum,
            pageSize = loadSize
        )
    }

    suspend fun commentReplyResult(
        oid: Long,
        pid: Long,
        key: CommentReplyCursor?
    ) = GrpcReplyClient(grpcClient).DetailList().runCatching {
        val req = DetailListReq(
            oid = oid,
            root = pid,
            type = 1,
            scene = DetailListScene.REPLY,
            cursor = key?.let {
                CursorReq(prev = it.prev, next = it.next, mode = Mode.fromValue(it.mode)!!)
            }
        )
        val res = executeAndSave(req)
        val data = res.root?.replies.orEmpty()
        val nextKey = res.cursor?.takeIf { !it.isEnd }?.let {
            CommentReplyCursor(it.mode.value, it.next, it.prev)
        }
        PagingData(data, nextKey)
    }

    suspend fun commentResult(
        id: String,
        nextParam: Long?
    ) = GrpcReplyClient(grpcClient).MainList().runCatching {
        val req = MainListReq(id.toLong(), 1, CursorReq(next = nextParam ?: 0, mode = Mode.MAIN_LIST_HOT))

        val res = executeAndSave(req)
        val data = res.replies
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
            mid,
            pageNum = key,
            pageSize = pageSize
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
                "load: $lastMax $lastTp"
            }
            val req = CursorV2Req(cursor = Cursor(max = lastMax, maxTp = lastTp), business = "archive")
            val res = executeAndSave(req)
            log {
                "load: ${res.cursor}"
            }
            val data = res.items
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
            val result = executeAndSave(req)
            if (result.dynamic_list != null) {
                val dynamicListData = result.dynamic_list
                val itemsList = dynamicListData.list.filter { item ->
                    item.card_type != DynamicType.dyn_none && item.card_type != DynamicType.ad
                }.map { item ->
                    val modules = item.modules
                    val userModule = modules.first { it.module_author != null }.module_author!!
                    val descModule = modules.find { it.module_desc != null }?.module_desc
                    val dynamicModule = modules.first { it.module_dynamic != null }.module_dynamic!!
                    val statModule = modules.first { it.module_stat != null }.module_stat!!
                    val author = userModule.author!!
                    MomentsDataInfo(
                        mid = author.mid.toString(),
                        name = author.name,
                        face = author.face,
                        labelText = userModule.ptime_label_text,
                        dynamicType = dynamicModule.type.value,
                        dynamicContent = dynamicModule.getDynamicContent(),
                        descMode = MomentsDesc(descModule?.text),
                        stat = MomentsStat(statModule.reply, statModule.like, statModule.repost),
                    )
                }
                PagingData(itemsList, (dynamicListData.history_offset to dynamicListData.update_baseline))
            } else {
                PagingData(emptyList(), null)
            }
        }

    suspend fun playList() = userspaceApi.serviceCatching {
        playList()
    }

    suspend fun requestQrcode() = authApi.qrCode()

    suspend fun requestQrcodeResult(authCode: String) = authApi.checkQrCode(authCode)

    suspend fun requestUserInfo(mid: Long) = userspaceApi.serviceCatching {
        space(mid.toString())
    }

    suspend fun searchUpper(
        keyword: String,
        loadSize: Int,
        pageNum: Int
    ) = searchApi.serviceCatching {
        searchUpper(
            keyword = keyword,
            pageNum = pageNum,
            pageSize = loadSize
        )
    }

    suspend fun searchVideo(
        keyword: String,
        pageNum: Int,
        pageSize: Int
    ) = searchApi.serviceCatching {
        searchArchive(
            keyword = keyword,
            order = "default",
            duration = 0,
            rid = 0,
            pageNum = pageNum,
            pageSize = pageSize
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
        quality: Int,
        fnval: Int,
        aid: String,
        id: String
    ) = playerApi.serviceCatching {
        getVideoPalyUrl(
            aid,
            id,
            quality.toString(),
            if (fnval > 2) "1" else null,
            fnval.toString()
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
