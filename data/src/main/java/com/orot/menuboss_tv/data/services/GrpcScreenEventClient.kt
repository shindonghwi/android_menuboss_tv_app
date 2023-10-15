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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit


class GrpcScreenEventClient : SafeGrpcRequest() {

    // status code = 701 // 서버 연결 실패.

    companion object {
        private const val TAG = "GrpcScreenEventClient"
    }

    private var connectChannel: ManagedChannel? = null
    private var contentChannel: ManagedChannel? = null

    private var connectBlockingStub: ScreenEventServiceGrpc.ScreenEventServiceStub? = null
    private var contentBlockingStub: ScreenEventServiceGrpc.ScreenEventServiceStub? = null

    private val _connectEvents =
        MutableStateFlow<Pair<ConnectEventResponse.ConnectEvent?, Int>?>(null)
    private val connectEvents: StateFlow<Pair<ConnectEventResponse.ConnectEvent?, Int>?> get() = _connectEvents

    private val _contentEvents =
        MutableStateFlow<Pair<ContentEventResponse.ContentEvent?, Int>?>(null)
    private val contentEvents: StateFlow<Pair<ContentEventResponse.ContentEvent?, Int>?> get() = _contentEvents

    private fun initConnectChannel(uuid: String) {
        if (connectChannel == null) {
            _connectEvents.tryEmit(null)
            Log.w(TAG, "initConnectChannel")

            connectChannel =
                ManagedChannelBuilder.forAddress("dev-screen-grpc.themenuboss.com", 443)
                    .useTransportSecurity()
                    .intercept(MetadataUtils.newAttachHeadersInterceptor(
                        Metadata().apply {
                            put(
                                Metadata.Key.of(
                                    "x-unique-id",
                                    Metadata.ASCII_STRING_MARSHALLER
                                ),
                                uuid
                            )
                        }
                    )).build()

            connectChannel?.let {
                // 기존 코루틴 스텁을 기본 스텁으로 변경합니다.
                connectBlockingStub = ScreenEventServiceGrpc.newStub(it)

                val responseObserver = object : StreamObserver<ConnectEventResponse> {
                    override fun onNext(value: ConnectEventResponse) {
                        Log.d(TAG, "Received response: ${value.event}")
                        _connectEvents.tryEmit(Pair(value.event, value.eventValue))

                        if (value.event == ConnectEventResponse.ConnectEvent.ENTRY) {
                            closeConnectChannel()
                        }

                    }

                    override fun onError(t: Throwable) {
                        Log.e(TAG, "Error in stream", t)
                        if (_connectEvents.value?.second != 701){
                            _connectEvents.tryEmit(Pair(null, 701))
                        }
                        closeConnectChannel()
                    }

                    override fun onCompleted() {
                        Log.d(TAG, "Stream completed")
                    }
                }

                connectBlockingStub?.connectStream(Empty.getDefaultInstance(), responseObserver)
            }
        }
    }

    private fun initContentChannel(accessToken: String) {
        if (contentChannel == null) {
            _contentEvents.tryEmit(null)
            Log.w(TAG, "initContentChannel")

            contentChannel =
                ManagedChannelBuilder.forAddress("dev-screen-grpc.themenuboss.com", 443)
                    .useTransportSecurity()
                    .intercept(MetadataUtils.newAttachHeadersInterceptor(
                        Metadata().apply {
                            put(
                                Metadata.Key.of(
                                    "Authorization",
                                    Metadata.ASCII_STRING_MARSHALLER
                                ),
                                accessToken
                            )
                        }
                    )).build()

            contentChannel?.let {
                contentBlockingStub = ScreenEventServiceGrpc.newStub(it)

                val responseObserver = object : StreamObserver<ContentEventResponse> {
                    override fun onNext(value: ContentEventResponse) {
                        Log.d(TAG, "Received response: ${value.event}")
                        _contentEvents.tryEmit(Pair(value.event, value.eventValue))

                        if (value.event == ContentEventResponse.ContentEvent.SCREEN_DELETED) {
                            closeConnectChannel()
                            closeContentChannel()
                        }
                    }

                    override fun onError(t: Throwable) {
                        Log.e(TAG, "Error in stream", t)
                        if (_contentEvents.value?.second != 701){
                            _contentEvents.tryEmit(Pair(null, 701))
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


    fun openConnectStream(uuid: String): StateFlow<Pair<ConnectEventResponse.ConnectEvent?, Int>?> {
        initConnectChannel(uuid)
        return connectEvents
    }


    fun openContentStream(accessToken: String): StateFlow<Pair<ContentEventResponse.ContentEvent?, Int>?> {
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
    }

}