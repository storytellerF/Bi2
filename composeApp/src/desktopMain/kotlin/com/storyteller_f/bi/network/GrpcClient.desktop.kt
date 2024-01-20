package com.storyteller_f.bi.network

import com.squareup.wire.GrpcClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

actual val grpcClient: GrpcClient
    get() = GrpcClient.Builder().client(
        OkHttpClient.Builder().addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder().apply {
                    grpcHeaderMap.forEach {
                        addHeader(it.key, it.value)
                    }
                }.build()
            )
        }.addInterceptor(ReplaceUrlInterceptor()).build()
    ).baseUrl(GRPC_BASE).build()

class ReplaceUrlInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newUrl =
            request.url.newBuilder().encodedPath(request.url.encodedPath.replace("interfaces", "interface")).build()
        val newRequest = request.newBuilder().url(newUrl).build()
        val response = chain.proceed(newRequest)
        return response
    }
}
