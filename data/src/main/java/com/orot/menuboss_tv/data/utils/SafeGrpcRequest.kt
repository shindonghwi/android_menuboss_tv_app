package com.orot.menuboss_tv.data.utils


import android.util.Log
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class SafeGrpcRequest {

    private val logTag = "SafeGrpcRequest"

    fun <T : Any> safeGrpcRequest(call: suspend () -> Flow<T>): Flow<T> = flow {
        try {
            Log.w(logTag, "safeGrpcRequest: start")
            call().collect { response ->
                Log.w(logTag, "safeGrpcRequest: start emit collect")
                emit(response)
            }
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