package com.orot.menuboss_tv.domain.usecases

import android.util.Log
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.repository.ScreenEventsRepository
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SubscribeContentStreamUseCase @Inject constructor(private val screenEventsRepository: ScreenEventsRepository) {
    operator fun invoke(uuid: String): Flow<Resource<ContentEventResponse.ContentEvent>> = flow {
        emit(Resource.Loading())
        screenEventsRepository.openContentStream(uuid).collect { response ->
            Log.w("SubscribeContentStream", "response ContentStream $response: ", )
            when (response.status) {
                200 -> emit(Resource.Success(response.message, response.data))
                else -> emit(Resource.Error(response.message))
            }
        }
    }
}
