package com.orot.menuboss_tv.data.services

import android.util.Log
import com.google.protobuf.Empty
import com.orot.menuboss_tv.data.utils.SafeGrpcRequest
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import com.orotcode.menuboss.grpc.lib.ScreenEventServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch


class GrpcScreenEventClient : SafeGrpcRequest() {
    private lateinit var metadata: Metadata
    private var connectChannel: ManagedChannel? = null
    private var contentChannel: ManagedChannel? = null

    private var connectBlockingStub: ScreenEventServiceGrpc.ScreenEventServiceStub? = null
    private var contentBlockingStub: ScreenEventServiceGrpc.ScreenEventServiceStub? = null

    private val TAG = "GrpcScreenEvent"

    private fun initConnectChannel(uuid: String) {
        if (connectChannel == null) {
            metadata = Metadata()
            val uuidKey = Metadata.Key.of("x-unique-id", Metadata.ASCII_STRING_MARSHALLER)
            metadata.put(uuidKey, uuid)
            Log.w(TAG, "initConnectChannel: $metadata")
            connectChannel =
                ManagedChannelBuilder.forAddress("dev-screen-grpc.themenuboss.com", 443)
                    .useTransportSecurity()
                    .intercept(MetadataUtils.newAttachHeadersInterceptor(metadata)).build()

            connectChannel?.let {
                // 기존 코루틴 스텁을 기본 스텁으로 변경합니다.
                connectBlockingStub = ScreenEventServiceGrpc.newStub(it)
            }
        }
    }

    private fun initContentChannel(accessToken: String) {
        if (contentChannel == null) {
            metadata = Metadata()
            val uuidKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)
            metadata.put(uuidKey, accessToken)
            Log.w(TAG, "initContentChannel: $metadata")
            contentChannel =
                ManagedChannelBuilder.forAddress("dev-screen-grpc.themenuboss.com", 443)
                    .useTransportSecurity()
                    .intercept(MetadataUtils.newAttachHeadersInterceptor(metadata)).build()

            contentChannel?.let {
                contentBlockingStub = ScreenEventServiceGrpc.newStub(it)
            }
        }
    }

    private val _connectEvents = MutableSharedFlow<ConnectEventResponse.ConnectEvent>()
    private val _contentEvents = MutableSharedFlow<ContentEventResponse.ContentEvent>()

    suspend fun openConnectStream(uuid: String): SharedFlow<ConnectEventResponse.ConnectEvent> {
        initConnectChannel(uuid)

        val responseObserver = object : StreamObserver<ConnectEventResponse> {
            override fun onNext(value: ConnectEventResponse) {
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d(TAG, "Received response: ${value.event}")

                    // 이벤트를 SharedFlow에 전달합니다.
                    _connectEvents.emit(value.event)

                    if (value.event == ConnectEventResponse.ConnectEvent.ENTRY) {
                        closeConnectChannel()
                    }
                }
            }

            override fun onError(t: Throwable) {
                Log.e(TAG, "Error in stream", t)
                closeConnectChannel()
            }

            override fun onCompleted() {
                Log.d(TAG, "Stream completed")
            }
        }

        connectBlockingStub?.connectStream(Empty.getDefaultInstance(), responseObserver)

        return _connectEvents
    }


    fun openContentStream(accessToken: String): SharedFlow<ContentEventResponse.ContentEvent> {
        initContentChannel(accessToken)

        val responseObserver = object : StreamObserver<ContentEventResponse> {
            override fun onNext(value: ContentEventResponse) {
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d(TAG, "Received response: ${value.event}")

                    // 이벤트를 SharedFlow에 전달합니다.
                    _contentEvents.emit(value.event)

                    if (value.event == ContentEventResponse.ContentEvent.SCREEN_DELETED) {
                        closeContentChannel()
                    }
                }
            }

            override fun onError(t: Throwable) {
                Log.e(TAG, "Error in stream", t)
                closeContentChannel()
            }

            override fun onCompleted() {
                Log.d(TAG, "Stream completed")
                closeContentChannel()
            }
        }

        contentBlockingStub?.contentStream(Empty.getDefaultInstance(), responseObserver)

        return _contentEvents
    }

    fun closeConnectChannel() {
        connectChannel?.shutdown()
        connectChannel = null
    }

    fun closeContentChannel() {
        contentChannel?.shutdown()
        contentChannel = null
    }

}