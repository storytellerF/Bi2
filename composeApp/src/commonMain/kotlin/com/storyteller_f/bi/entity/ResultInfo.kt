package com.storyteller_f.bi.entity

import kotlinx.serialization.Serializable

interface Response<T> {

    val code: Int
    val message: String
    val res: T?

    fun isSuccess() = code == 0
}

@Serializable
data class ResultInfo<T>(
    override val code: Int,
    val `data`: T? = null,
    override val message: String,
    val ttl: Int,
) : Response<T> {
    override val res: T?
        get() = data
}

@Serializable
data class ResultInfo2<T>(
    override val code: Int,
    override val message: String,
    val result: T?,
    val ttl: Int,
) : Response<T> {
    override val res: T?
        get() = result
}


data class PagingData<K, T>(val data: List<T>, val pagination: K?)