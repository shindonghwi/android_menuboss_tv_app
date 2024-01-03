package com.orot.menuboss_tv_kr.data.repository

import com.orot.menuboss_tv_kr.data.services.GrpcScreenEventClient
import com.orot.menuboss_tv_kr.domain.repository.ScreenEventsRepository
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import com.orotcode.menuboss.grpc.lib.PlayingEventRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScreenEventsRepositoryImpl @Inject constructor(private val grpcClient: GrpcScreenEventClient) :
    ScreenEventsRepository {

    override suspend fun openConnectStream(uuid: String): Flow<Pair<ConnectEventResponse.ConnectEvent?, Int>> =
        grpcClient.openConnectStream(uuid)

    override suspend fun openContentStream(accessToken: String): Flow<Pair<ContentEventResponse.ContentEvent?, Int>> =
        grpcClient.openContentStream(accessToken)

    override suspend fun sendPlayingEvent(playingEvent: PlayingEventRequest) {
        grpcClient.sendPlayingEvent(playingEvent)
    }

    override suspend fun closeStream() {
        grpcClient.run {
            closeConnectChannel()
            closeContentChannel()
            closePlayingChannel()
        }
    }
}
