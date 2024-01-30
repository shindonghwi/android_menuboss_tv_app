package com.orot.menuboss_tv_kr.domain.usecases

import com.orot.menuboss_tv_kr.domain.repository.ScreenEventsRepository
import javax.inject.Inject

class UnSubscribeStreamUseCase @Inject constructor(private val screenEventsRepository: ScreenEventsRepository) {
    suspend operator fun invoke() = screenEventsRepository.closeStream()
}
