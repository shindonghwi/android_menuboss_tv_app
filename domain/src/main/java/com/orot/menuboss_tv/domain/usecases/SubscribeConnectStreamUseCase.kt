package com.orot.menuboss_tv.domain.usecases

import com.orot.menuboss_tv.domain.repository.ScreenEventsRepository
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscribeConnectStreamUseCase @Inject constructor(private val screenEventsRepository: ScreenEventsRepository) {
    suspend operator fun invoke(uuid: String): Flow<Pair<ConnectEventResponse.ConnectEvent?, Int>?> =
        screenEventsRepository.openConnectStream(uuid)
}
