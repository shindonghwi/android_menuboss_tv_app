package com.orot.menuboss_tv_kr.data.utils


import android.util.Log
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

abstract class SafeGrpcRequest {

    private val logTag = "SafeGrpcRequest"

    fun <T : Any> safeGrpcRequest(call: suspend ProducerScope<T>.() -> Unit): Flow<T> = channelFlow {
        try {
            Log.w(logTag, "safeGrpcRequest: start")
            call()
        } catch (e: StatusRuntimeException) {
            Log.w(logTag, "safeGrpcRequest: e: $e")
            when (e.status.code) {
                io.grpc.Status.UNAUTHENTICATED.code -> {
                    Log.d("SafeGrpcRequest", "Unauthenticated request: ${e.message}")
                }

                else -> {
                    Log.d("SafeGrpcRequest", "gRPC request failed: ${e.message}")
                }
            }
            throw GrpcException(e.message ?: "Unknown gRPC error")
        }
    }
}

class GrpcException(message: String) : Exception(message)