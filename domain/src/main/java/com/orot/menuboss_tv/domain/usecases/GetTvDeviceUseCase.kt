package com.orot.menuboss_tv.domain.usecases

import android.util.Log
import com.orot.menuboss_tv.domain.entities.DeviceInfo
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.repository.TvRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTvDeviceUseCase @Inject constructor(private val tvRepository: TvRepository) {
    suspend operator fun invoke(uuid: String): Flow<Resource<DeviceInfo>> = flow {
        emit(Resource.Loading())
        try {
            val response = tvRepository.getDeviceInfo(uuid)
            when (response.status) {
                200 -> emit(Resource.Success(response.message, response.data))
                else -> emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(message = e.toString()))
        }
    }
}