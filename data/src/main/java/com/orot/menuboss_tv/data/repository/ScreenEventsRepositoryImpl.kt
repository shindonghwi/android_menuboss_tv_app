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
    private val delayTimeMillis = 3000L  // 재시도 전 대기 시간

    override suspend fun openConnectStream(uuid: String): Flow<ApiResponse<ConnectEventResponse.ConnectEvent>> =
        flow {
            var shouldBreakLoop = false
            while (!shouldBreakLoop) {  // 무한 재시도
                try {
                    grpcClient.openConnectStream(uuid).collect { response ->
                        Log.w(logTag, "openConnectStreamImpl with response: $response")
                        emit(ApiResponse(status = 200, message = "", data = response))

                        if (response == ConnectEventResponse.ConnectEvent.ENTRY) {
                            shouldBreakLoop = true
                            emit(ApiResponse(status = 500, message = "", data = response))
                            return@collect
                        }
                    }
                } catch (e: Exception) {
                    Log.w(logTag, "Connection error: $e")
                    delay(delayTimeMillis)  // 재시도 전 일정 시간 대기
                }
            }
        }

    override suspend fun openContentStream(accessToken: String): Flow<ApiResponse<ContentEventResponse.ContentEvent>> =
        flow {
            var shouldBreakLoop = false

            while (!shouldBreakLoop) {  // 무한 재시도 조건에 플래그 사용
                try {
                    grpcClient.openContentStream(accessToken).collect { response ->
                        Log.w(logTag, "openContentStreamImpl with response: $response")
                        emit(ApiResponse(status = 200, message = "", data = response))

                        if (response == ContentEventResponse.ContentEvent.SCREEN_DELETED) {
                            emit(ApiResponse(status = 500, message = "", data = response))
                            shouldBreakLoop = true
                            return@collect
                        }
                    }
                } catch (e: Exception) {
                    Log.w(logTag, "Connection error: $e")
                    delay(delayTimeMillis)  // 재시도 전 일정 시간 대기
                }
            }
        }

}
