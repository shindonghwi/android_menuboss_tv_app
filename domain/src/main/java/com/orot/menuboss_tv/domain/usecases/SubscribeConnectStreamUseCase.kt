package com.orot.menuboss_tv.domain.usecases

import android.util.Log
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.repository.ScreenEventsRepository
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SubscribeConnectStreamUseCase @Inject constructor(private val screenEventsRepository: ScreenEventsRepository) {
    operator fun invoke(uuid: String): Flow<Resource<ConnectEventResponse.ConnectEvent>> = flow {
        emit(Resource.Loading())
        screenEventsRepository.openConnectStream(uuid).collect { response ->
            Log.w("SubscribeConnectStream", "response ConnectStream ${response.status}: ", )
            when (response.status) {
                200 -> emit(Resource.Success(response.message, response.data))
                else -> emit(Resource.Error(response.message))
            }
        }
    }
}
