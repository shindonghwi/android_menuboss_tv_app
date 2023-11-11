package com.orot.menuboss_tv.domain.repository

import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import kotlinx.coroutines.flow.Flow

interface ScreenEventsRepository {
    suspend fun openConnectStream(uuid: String): Flow<Pair<ConnectEventResponse.ConnectEvent?, Int>>
    suspend fun openContentStream(accessToken: String): Flow<Pair<ContentEventResponse.ContentEvent?, Int>?>
}