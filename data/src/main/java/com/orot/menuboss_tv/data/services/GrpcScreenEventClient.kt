package com.orot.menuboss_tv.data.services

import android.util.Log
import com.google.protobuf.Empty
import com.orot.menuboss_tv.data.utils.SafeGrpcRequest
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import com.orotcode.menuboss.grpc.lib.ScreenEventServiceGrpcKt
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GrpcScreenEventClient : SafeGrpcRequest() {

    private lateinit var metadata: Metadata
    private lateinit var connectChannel: ManagedChannel
    private lateinit var contentChannel: ManagedChannel
    private lateinit var stub: ScreenEventServiceGrpcKt.ScreenEventServiceCoroutineStub

    private fun initConnectChannel(uuid: String) {
        if (::connectChannel.isInitialized.not() || connectChannel.isTerminated || connectChannel.isShutdown){
            metadata = Metadata()
            val uuidKey = Metadata.Key.of("x-unique-id", Metadata.ASCII_STRING_MARSHALLER)
            metadata.put(uuidKey, uuid)
            connectChannel = ManagedChannelBuilder.forAddress("dev-screen-grpc.themenuboss.com", 443)
                .useTransportSecurity()
                .intercept(MetadataUtils.newAttachHeadersInterceptor(metadata))
                .build()

            stub = ScreenEventServiceGrpcKt.ScreenEventServiceCoroutineStub(connectChannel)
        }
    }
    private fun initContentChannel(accessToken: String) {
        if (::contentChannel.isInitialized.not() || contentChannel.isTerminated || contentChannel.isShutdown){
            metadata = Metadata()
            val uuidKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)
            metadata.put(uuidKey, accessToken)
            contentChannel = ManagedChannelBuilder.forAddress("dev-screen-grpc.themenuboss.com", 443)
                .useTransportSecurity()
                .intercept(MetadataUtils.newAttachHeadersInterceptor(metadata))
                .build()

            stub = ScreenEventServiceGrpcKt.ScreenEventServiceCoroutineStub(contentChannel)
        }
    }

    suspend fun openConnectStream(uuid: String): Flow<ConnectEventResponse.ConnectEvent> = safeGrpcRequest {
        initConnectChannel(uuid)
        Log.w("GrpcScreenEvent", "openConnectStream Starting connect")
        flow {
            stub.connectStream(Empty.getDefaultInstance()).collect { response ->
                Log.w("GrpcScreenEvent", "Connected: ${response.event}")
                emit(response.event)
            }
        }
    }
    suspend fun openContentStream(accessToken: String): Flow<ContentEventResponse.ContentEvent> = safeGrpcRequest {
        initContentChannel(accessToken)
        Log.w("GrpcScreenEvent", "openContentStream Starting connect")
        flow {
            stub.contentStream(Empty.getDefaultInstance()).collect { response ->
                Log.w("GrpcScreenEvent", "Connected: ${response.event}")
                emit(response.event)
            }
        }
    }
}
