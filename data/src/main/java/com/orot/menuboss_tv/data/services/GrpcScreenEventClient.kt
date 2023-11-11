package com.orot.menuboss_tv.data.services

import android.util.Log
import com.google.protobuf.Empty
import com.orot.menuboss_tv.data.utils.SafeGrpcRequest
import com.orot.menuboss_tv.domain.constants.GRPC_BASE_URL
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import com.orotcode.menuboss.grpc.lib.ScreenEventServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class GrpcScreenEventClient : SafeGrpcRequest() {

    // status -> 0: 초기상태, 1: 연결 성공, 2: 연결 실패, 3: 이벤트 수신

    companion object {
        private const val TAG = "GrpcScreenEventClient"
        private const val INITIAL_RETRY_DELAY = 2000L // 초기 지연 시간 2초
        private const val MAX_RETRY_DELAY = 30000L // 최대 지연 시간 60초
    }

    private var connectChannel: ManagedChannel? = null
    private var contentChannel: ManagedChannel? = null

    private var connectBlockingStub: ScreenEventServiceGrpc.ScreenEventServiceStub? = null
    private var contentBlockingStub: ScreenEventServiceGrpc.ScreenEventServiceStub? = null


    private var lastContentEventValue: Int? = null

    private val _connectEvents = MutableSharedFlow<Pair<ConnectEventResponse.ConnectEvent?, Int>>(
        replay = 1, extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val connectEvents: Flow<Pair<ConnectEventResponse.ConnectEvent?, Int>> get() = _connectEvents.asSharedFlow()

    private val _contentEvents = MutableSharedFlow<Pair<ContentEventResponse.ContentEvent?, Int>?>(
        replay = 1, extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val contentEvents: Flow<Pair<ContentEventResponse.ContentEvent?, Int>?> get() = _contentEvents.asSharedFlow()

    private fun initConnectChannel(uuid: String) {
        if (connectChannel == null) {
            _connectEvents.tryEmit(Pair(null, 0))
            Log.w(TAG, "initConnectChannel")

            connectChannel = ManagedChannelBuilder.forAddress(GRPC_BASE_URL, 443)
                .useTransportSecurity()
                .intercept(MetadataUtils.newAttachHeadersInterceptor(
                    Metadata().apply {
                        put(Metadata.Key.of("x-unique-id", Metadata.ASCII_STRING_MARSHALLER), uuid)
                    }
                )).build()

            connectChannel?.let {
                connectBlockingStub = ScreenEventServiceGrpc.newStub(it)
                startConnectStream(uuid)
            }
        }
    }


    private fun startConnectStream(uuid: String) {
        var isConnected = true  // 연결 성공 여부를 추적하는 플래그
        var retryDelay = INITIAL_RETRY_DELAY

        val responseObserver = object : StreamObserver<ConnectEventResponse> {
            override fun onNext(value: ConnectEventResponse) {
                Log.d(TAG, "Received response: ${value.event}")
                _connectEvents.tryEmit(Pair(value.event, 3))

                if (value.event == ConnectEventResponse.ConnectEvent.ENTRY) {
                    closeConnectChannel()
                }
            }

            override fun onError(t: Throwable) {
                Log.e(TAG, "Error in stream", t)
                isConnected = false
                closeConnectChannel()

                CoroutineScope(Dispatchers.IO).launch {
                    delay(retryDelay)  // 지연을 추가
                    retryDelay = (retryDelay * 2).coerceAtMost(MAX_RETRY_DELAY)
                    initConnectChannel(uuid)  // 재시도
                }
            }

            override fun onCompleted() {
                Log.d(TAG, "Stream completed")
            }
        }

        connectBlockingStub?.connectStream(Empty.getDefaultInstance(), responseObserver)

        // 연결 성공 처리를 위한 지연 로직
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (isConnected){
                    delay(3000)  // 3초 대기
                    _connectEvents.tryEmit(Pair(null, 1)) // 연결 설공 이벤트 전달
                }
            } catch (e: CancellationException) {
                Log.e(TAG, "Connection check was cancelled", e)
            }
        }
    }

    private fun initContentChannel(accessToken: String) {
        if (contentChannel == null) {
            _contentEvents.tryEmit(null)
            Log.w(TAG, "initContentChannel")

            contentChannel = ManagedChannelBuilder.forAddress(GRPC_BASE_URL, 443)
                .useTransportSecurity()
                .intercept(MetadataUtils.newAttachHeadersInterceptor(
                    Metadata().apply {
                        put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), accessToken)
                    }
                )).build()

            contentChannel?.let {
                contentBlockingStub = ScreenEventServiceGrpc.newStub(it)

                val responseObserver = object : StreamObserver<ContentEventResponse> {
                    override fun onNext(value: ContentEventResponse) {
                        Log.d(TAG, "Received response: ${value.event}")
                        lastContentEventValue = value.eventValue
                        _contentEvents.tryEmit(Pair(value.event, value.eventValue))

                        if (value.event == ContentEventResponse.ContentEvent.SCREEN_DELETED) {
                            closeConnectChannel()
                            closeContentChannel()
                        }
                    }

                    override fun onError(t: Throwable) {
                        Log.e(TAG, "Error in stream", t)
                        if (lastContentEventValue != 701) {
                            _contentEvents.tryEmit(Pair(null, 701))
                            lastContentEventValue = 701
                        }
                        closeConnectChannel()
                        closeContentChannel()
                    }

                    override fun onCompleted() {
                        Log.d(TAG, "Stream completed")
                    }
                }

                contentBlockingStub?.contentStream(Empty.getDefaultInstance(), responseObserver)
            }
        }
    }

    fun openConnectStream(uuid: String): Flow<Pair<ConnectEventResponse.ConnectEvent?, Int>> {
        initConnectChannel(uuid)
        return connectEvents
    }

    fun openContentStream(accessToken: String): Flow<Pair<ContentEventResponse.ContentEvent?, Int>?> {
        initContentChannel(accessToken)
        return contentEvents
    }


    fun closeConnectChannel() {
        connectChannel?.shutdown()
        connectChannel = null
        connectBlockingStub = null
    }

    fun closeContentChannel() {
        contentChannel?.shutdown()
        contentChannel = null
        contentBlockingStub = null
        lastContentEventValue = null
    }

}