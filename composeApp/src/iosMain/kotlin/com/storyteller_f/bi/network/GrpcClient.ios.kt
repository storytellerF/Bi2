package com.storyteller_f.bi.network

import com.squareup.wire.*

actual val grpcClient: GrpcClient
    get() = object : GrpcClient() {
        override fun <S : Any, R : Any> newCall(method: GrpcMethod<S, R>) = method.grpcCall()

        override fun <S : Any, R : Any> newStreamingCall(method: GrpcMethod<S, R>): GrpcStreamingCall<S, R> =
            method.grpcStreamingCall()
    }
