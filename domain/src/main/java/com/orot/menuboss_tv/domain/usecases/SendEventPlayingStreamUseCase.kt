package com.orot.menuboss_tv.domain.usecases

import com.orot.menuboss_tv.domain.repository.ScreenEventsRepository
import com.orotcode.menuboss.grpc.lib.PlayingEventRequest
import javax.inject.Inject

class SendEventPlayingStreamUseCase @Inject constructor(private val screenEventsRepository: ScreenEventsRepository) {
    suspend operator fun invoke(event: PlayingEventRequest) = screenEventsRepository.sendPlayingEvent(event)
}
