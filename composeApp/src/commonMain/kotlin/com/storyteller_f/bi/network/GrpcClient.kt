package com.storyteller_f.bi.network

import com.squareup.wire.*
import com.storyteller_f.bi.gs.LoginInfoState
import com.storyteller_f.bi.gs.getOrCreateBuvidId
import com.storyteller_f.bi.userAgent
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.io.readByteArray
import okio.Timeout

expect val grpcClient: GrpcClient

val grpcHttpClient by lazy {
    HttpClient {
        defaultRequest {
            headers {
                grpcHeaderMap.forEach {
                    append(it.key, it.value)
                }
            }
        }
    }
}

fun <R : Any, S : Any> GrpcMethod<S, R>.grpcCall(): GrpcCall<S, R> {
    val method = this
    return object : GrpcCall<S, R> {
        override val method: GrpcMethod<S, R> = method
        override var requestMetadata: Map<String, String> = emptyMap()
        override var responseMetadata: Map<String, String>? = null
        private lateinit var job: Job
        override val timeout: Timeout
            get() = TODO("Not yet implemented")

        override fun cancel() = job.cancel()

        override fun clone(): GrpcCall<S, R> {
            TODO("Not yet implemented")
        }

        override fun isCanceled() = ::job.isInitialized && job.isCancelled

        override fun isExecuted() = ::job.isInitialized

        override fun executeBlocking(request: S) = runBlocking {
            execute(request)
        }

        override suspend fun execute(request: S) = r(request)

        private suspend fun r(request: S, callback: GrpcCall.Callback<S, R>? = null): R {
            val response = method.httpResponse(request)
            responseMetadata = response.headersMap()
            val body = response.body<ByteArray>()
            val decode = method.responseAdapter.decode(body)
            callback?.onSuccess(this, decode)
            return decode
        }

        override fun enqueue(request: S, callback: GrpcCall.Callback<S, R>) {
            runBlocking {
                r(request, callback)
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun <R : Any, S : Any> GrpcMethod<S, R>.grpcStreamingCall(): GrpcStreamingCall<S, R> {
    val method = this
    return object : GrpcStreamingCall<S, R> {
        override val method: GrpcMethod<S, R> = method
        override var requestMetadata: Map<String, String> = emptyMap()
        override var responseMetadata: Map<String, String>? = null
        private lateinit var job: Job
        override val timeout: Timeout
            get() = TODO("Not yet implemented")

        override fun cancel() = job.cancel()

        override fun clone(): GrpcStreamingCall<S, R> {
            TODO("Not yet implemented")
        }

        override fun isCanceled() = ::job.isInitialized && job.isCancelled

        override fun isExecuted() = ::job.isInitialized

        @Deprecated(
            "Provide a scope, preferably not GlobalScope",
            replaceWith = ReplaceWith("executeIn(GlobalScope)", "kotlinx.coroutines.GlobalScope"),
            level = DeprecationLevel.WARNING
        )
        override fun execute(): Pair<SendChannel<S>, ReceiveChannel<R>> {
            return runBlocking {
                executeIn(this)
            }
        }

        override fun executeBlocking(): Pair<MessageSink<S>, MessageSource<R>> {
            TODO("")
//            val sink = Buffer()
//            val grpcMessageSink = GrpcMessageSink(sink, 0, method.requestAdapter, null, "")
//            val bufferedSource = Buffer()
//            val grpcMessageSource = GrpcMessageSource(bufferedSource, method.responseAdapter, null)
//            runBlocking {
//                val (sendChannel, receiveChannel) = executeIn(this)
//                launch {
//                    for (r in receiveChannel) {
//                        bufferedSource.write(method.responseAdapter.encode(r))
//                    }
//                }
//                launch {
//                    val array = ByteArray(10240)
//                    while (true) {
//                        val offset = sink.read(array)
//                        if (offset != -1) {
//                            sendChannel.send(method.requestAdapter.decode(array.copyOfRange(0, offset)))
//                        }
//                    }
//                }
//            }
//            return grpcMessageSink to grpcMessageSource
        }

        override fun executeIn(scope: CoroutineScope): Pair<SendChannel<S>, ReceiveChannel<R>> {
            val requestChannel = Channel<S>(1)
            val responseChannel = Channel<R>(1)
            val launch = scope.launch {
                method.httpStatement(requestChannel).execute {
                    responseMetadata = it.headersMap()
                    val channel: ByteReadChannel = it.body()
                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(1024)
                        while (!packet.exhausted()) {
                            val bytes = packet.readByteArray()
                            responseChannel.send(method.responseAdapter.decode(bytes))
                        }
                    }
                }
            }
            responseChannel.invokeOnClose {
                if (responseChannel.isClosedForReceive) {
                    // Short-circuit the request stream if it's still active.
                    launch.cancel()
                    requestChannel.cancel()
                }
            }
            return requestChannel to responseChannel
        }
    }
}

private fun HttpResponse.headersMap() = headers.toMap().mapValues {
    it.value.first()
}

private suspend fun<S : Any, R : Any> GrpcMethod<S, R>.httpResponse(request: S) = grpcHttpClient.post {
    url {
        path(path)
    }
    setBody(requestAdapter.encode(request))
}

private suspend fun<S : Any, R : Any> GrpcMethod<S, R>.httpStatement(requestChannel: Channel<S>) =
    grpcHttpClient.preparePost {
        url {
            path(path)
        }
        setBody(requestChannel)
    }

object GrpcHeader {
    const val IDENTIFY = "identify_v1"
    const val USER_AGENT = "User-Agent"
    const val APP_KEY = "APP-KEY"
    const val BILI_META = "x-bili-metadata-bin"
    const val AUTHORIZATION = "authorization"
    const val BILI_DEVICE = "x-bili-device-bin"
    const val BILI_NETWORK = "x-bili-network-bin"
    const val BILI_RESTRICTION = "x-bili-restriction-bin"
    const val BILI_LOCALE = "x-bili-locale-bin"
    const val BILI_FAWKES = "x-bili-fawkes-req-bin"
    const val BILI_MID = "x-bili-mid"
    const val GRPC_ACCEPT_ENCODING_KEY = "grpc-accept-encoding"
    const val GRPC_ACCEPT_ENCODING_VALUE = "identity,deflate,gzip"
    const val GRPC_TIME_OUT_KEY = "grpc-timeout"
    const val GRPC_TIME_OUT_VALUE = "20100m"
    const val ENVIRONMENT = "env"
    const val TRANSFER_ENCODING_KEY = "Transfer-Encoding"
    const val TRANSFER_ENCODING_VALUE = "chunked"
    const val BUVID = "buvid"
}

val grpcHeaderMap: Map<String, String>
    get() = mutableMapOf<String, String>().apply {
        val tokenInfo = LoginInfoState.loginInfo?.tokenInfo
        val token = tokenInfo?.accessToken ?: ""
        if (token.isNotBlank()) {
            put(GrpcHeader.AUTHORIZATION, GrpcHeader.IDENTIFY + " " + token)
            tokenInfo?.let {
                put(GrpcHeader.BILI_MID, it.mid.toString())
            }
        }
        put(GrpcHeader.USER_AGENT, userAgent)
        put(GrpcHeader.APP_KEY, GrpcBinary.MOBILE_APP)
        put(GrpcHeader.BILI_DEVICE, GrpcBinary.getDeviceBin())
        put(GrpcHeader.BILI_FAWKES, GrpcBinary.getFawkesreqBin())
        put(GrpcHeader.BILI_LOCALE, GrpcBinary.getLocaleBin())
        put(GrpcHeader.BILI_META, GrpcBinary.getMetadataBin(token))
        put(GrpcHeader.BILI_NETWORK, GrpcBinary.getNetworkBin())
        put(GrpcHeader.BILI_RESTRICTION, GrpcBinary.getRestrictionBin())
//        put(GrpcHeader.GRPC_ACCEPT_ENCODING_KEY, GrpcHeader.GRPC_ACCEPT_ENCODING_VALUE)
        put(GrpcHeader.GRPC_TIME_OUT_KEY, GrpcHeader.GRPC_TIME_OUT_VALUE)
        put(GrpcHeader.ENVIRONMENT, GrpcBinary.ENVIRONMENT)
        put(GrpcHeader.TRANSFER_ENCODING_KEY, GrpcHeader.TRANSFER_ENCODING_VALUE)
        put(GrpcHeader.BUVID, getOrCreateBuvidId())
    }.toMap()
