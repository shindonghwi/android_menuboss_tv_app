package com.orot.menuboss_tv.data.repository

import android.util.Log
import com.orot.menuboss_tv.data.services.GrpcScreenEventClient
import com.orot.menuboss_tv.domain.entities.ApiResponse
import com.orot.menuboss_tv.domain.repository.ScreenEventsRepository
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ScreenEventsRepositoryImpl @Inject constructor(private val grpcClient: GrpcScreenEventClient) :
    ScreenEventsRepository {

    private val logTag = "ScreenEventsRepo"

    override suspend fun openConnectStream(uuid: String): Flow<ApiResponse<ConnectEventResponse.ConnectEvent>> = flow {
        try {
            grpcClient.openConnectStream(uuid).collect { response ->
                Log.w(logTag, "Connected with response: ${response}")
                emit(ApiResponse(status = 200, message = "Connected", data = response))
            }
        } catch (e: Exception) {
            Log.w(logTag, "Connection error: $e")
            emit(ApiResponse(status = 500, message = "Connection failed: ${e.message}", data = null))
        }
    }

    override suspend fun openContentStream(accessToken: String): Flow<ApiResponse<ContentEventResponse.ContentEvent>> = flow {
        try {
            grpcClient.openContentStream(accessToken).collect { response ->
                Log.w(logTag, "Connected with response: ${response}")
                emit(ApiResponse(status = 200, message = "Connected", data = response))
            }
        } catch (e: Exception) {
            Log.w(logTag, "Connection error: $e")
            emit(ApiResponse(status = 500, message = "Connection failed: ${e.message}", data = null))
        }
    }
}
