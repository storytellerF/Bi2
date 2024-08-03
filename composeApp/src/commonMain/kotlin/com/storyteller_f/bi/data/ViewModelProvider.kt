package com.storyteller_f.bi.data

import androidx.compose.runtime.Composable
import com.storyteller_f.bi.components.*
import com.storyteller_f.common.CreationExtras
import com.storyteller_f.common.MutableCreationExtras
import com.storyteller_f.common.buildExtras

import kotlin.reflect.KClass
import kotlin.reflect.cast

typealias ViewModel = moe.tlaster.precompose.viewmodel.ViewModel

object FavoriteIdKey : CreationExtras.Key<String>

object VideoId : CreationExtras.Key<String>
object SeasonId : CreationExtras.Key<String>
object VideoIdLong : CreationExtras.Key<Long>
object CommentId : CreationExtras.Key<Long>

fun <T : ViewModel> build(modelClass: KClass<T>, extras: CreationExtras): T? {
    val vm = when (modelClass) {
        FavoriteDetailViewModel::class -> FavoriteDetailViewModel(extras[FavoriteIdKey]!!)
        VideoViewModel::class -> VideoViewModel(extras[VideoId]!!)
        CommentViewModel::class -> CommentViewModel(extras[VideoId]!!)
        CommentReplyViewModel::class -> CommentReplyViewModel(
            extras[VideoIdLong]!!,
            extras[CommentId]!!,
        )

        VideoSearchViewModel::class -> VideoSearchViewModel()
        UserBannerViewModel::class -> UserBannerViewModel()
        BangumiViewModel::class -> BangumiViewModel(extras[VideoId]!!, extras[SeasonId]!!)
        QrcodeLoginViewModel::class -> QrcodeLoginViewModel()
        ToBePlayedViewModel::class -> ToBePlayedViewModel()
        HistoryViewModel::class -> HistoryViewModel()
        else -> null
    } ?: return null
    return modelClass.cast(vm)
}

@Composable
fun <T : ViewModel> viewModel(
    clazz: KClass<T>,
    keys: List<Any> = emptyList(),
    extras: MutableCreationExtras.() -> Unit = {}
): T {
    return moe.tlaster.precompose.viewmodel.viewModel(clazz, keys) {
        build(clazz, buildExtras(extras))!!
    }
}