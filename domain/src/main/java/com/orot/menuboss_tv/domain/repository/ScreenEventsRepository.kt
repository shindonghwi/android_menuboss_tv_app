package com.orot.menuboss_tv.domain.repository

import com.orot.menuboss_tv.domain.entities.ApiResponse
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import kotlinx.coroutines.flow.Flow

interface ScreenEventsRepository {
    suspend fun openConnectStream(uuid: String): Flow<ApiResponse<ConnectEventResponse.ConnectEvent>>
    suspend fun openContentStream(accessToken: String): Flow<ApiResponse<ContentEventResponse.ContentEvent>>
}