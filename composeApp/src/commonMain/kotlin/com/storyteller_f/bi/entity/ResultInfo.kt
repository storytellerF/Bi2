package com.storyteller_f.bi.entity

import kotlinx.serialization.Serializable

@Serializable
data class ResultInfo<T>(
    val code: Int,
    val message: String,
    val result: T? = null,
    val `data`: T? = null,
    val ttl: Int,
) {
    val isSuccess = code == 0
    val res: T?
        get() = data ?: result
}

data class PagingData<K, T>(val data: List<T>, val pagination: K?)
