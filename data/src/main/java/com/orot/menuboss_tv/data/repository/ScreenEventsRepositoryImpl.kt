package com.orot.menuboss_tv.data.repository

import android.util.Log
import com.orot.menuboss_tv.data.services.GrpcScreenEventClient
import com.orot.menuboss_tv.domain.entities.ApiResponse
import com.orot.menuboss_tv.domain.repository.ScreenEventsRepository
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ScreenEventsRepositoryImpl @Inject constructor(private val grpcClient: GrpcScreenEventClient) :
    ScreenEventsRepository {

    private val logTag = "ScreenEventsRepo"

    override suspend fun openConnectStream(uuid: String): Flow<ApiResponse<ConnectEventResponse.ConnectEvent>> = flow {
        val delayTimeMillis = 3000L  // 재시도 전 대기 시간
        var keepTrying = true

        while (keepTrying) {
            try {
                grpcClient.openConnectStream(uuid).collect { response ->
                    Log.w(logTag, "openConnectStreamImpl with response: ${response}")
                    emit(ApiResponse(status = 200, message = "Connected", data = response))
                    keepTrying = false  // 성공적으로 연결되면 루프 종료를 위해 flag 변경
                }
            } catch (e: Exception) {
                Log.w(logTag, "Connection error: $e")
                delay(delayTimeMillis)  // 재시도 전 일정 시간 대기
            }
        }
    }

    override suspend fun openContentStream(accessToken: String): Flow<ApiResponse<ContentEventResponse.ContentEvent>> = flow {
        val delayTimeMillis = 3000L  // 재시도 전 대기 시간
        var keepTrying = true

        while (keepTrying) {
            try {
                grpcClient.openContentStream(accessToken).collect { response ->
                    Log.w(logTag, "openContentStreamImpl with response: ${response}")
                    emit(ApiResponse(status = 200, message = "Connected", data = response))
                    keepTrying = false  // 성공적으로 연결되면 루프 종료를 위해 flag 변경
                }
            } catch (e: Exception) {
                Log.w(logTag, "Connection error: $e")
                delay(delayTimeMillis)  // 재시도 전 일정 시간 대기
            }
        }
    }


}
