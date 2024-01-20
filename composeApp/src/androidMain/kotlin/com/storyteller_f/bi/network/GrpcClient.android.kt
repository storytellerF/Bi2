package com.storyteller_f.bi.network

import com.squareup.wire.GrpcClient
import okhttp3.OkHttpClient

actual val grpcClient: GrpcClient
    get() = GrpcClient.Builder().client(OkHttpClient.Builder().addInterceptor {
        it.proceed(it.request().newBuilder().apply {
            grpcHeaderMap.forEach {
                addHeader(it.key, it.value)
            }
        }.build())
    }.build()).baseUrl(GRPC_BASE).build()