package com.orot.menuboss_tv.domain.usecases

import com.orot.menuboss_tv.domain.repository.ScreenEventsRepository
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscribeContentStreamUseCase @Inject constructor(private val screenEventsRepository: ScreenEventsRepository) {
    suspend operator fun invoke(accessToken: String): Flow<Pair<ContentEventResponse.ContentEvent?, Int>?> =
        screenEventsRepository.openContentStream(accessToken)
}