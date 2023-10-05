package com.orot.menuboss_tv.domain.usecases

import com.orot.menuboss_tv.domain.repository.ScreenEventsRepository
import javax.inject.Inject

class UnSubscribeConnectStreamUseCase @Inject constructor(private val screenEventsRepository: ScreenEventsRepository) {
    suspend operator fun invoke() =
        screenEventsRepository.cancelConnectStream()
}
