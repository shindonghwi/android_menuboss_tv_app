package com.orot.menuboss_tv.data.repository

import com.orot.menuboss_tv.data.services.GrpcScreenEventClient
import com.orot.menuboss_tv.domain.repository.ScreenEventsRepository
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScreenEventsRepositoryImpl @Inject constructor(private val grpcClient: GrpcScreenEventClient) :
    ScreenEventsRepository {

    override suspend fun openConnectStream(uuid: String): Flow<Pair<ConnectEventResponse.ConnectEvent?, Int>?> =
        grpcClient.openConnectStream(uuid)

    override suspend fun openContentStream(accessToken: String): Flow<Pair<ContentEventResponse.ContentEvent?, Int>?> =
        grpcClient.openContentStream(accessToken)
}
