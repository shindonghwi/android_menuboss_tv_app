package com.orot.menuboss_tv_kr.domain.usecases.remote

import com.orot.menuboss_tv_kr.domain.entities.DevicePlaylistModel
import com.orot.menuboss_tv_kr.domain.entities.Resource
import com.orot.menuboss_tv_kr.domain.repository.TvRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPlaylistUseCase @Inject constructor(private val tvRepository: TvRepository) {
    suspend operator fun invoke(uuid: String, accessToken: String): Flow<Resource<DevicePlaylistModel>> =
        flow {
            emit(Resource.Loading())
            try {
                val response = tvRepository.getDevicePlaylist(uuid, accessToken)
                when (response.status) {
                    in 200..299 -> emit(
                        Resource.Success(
                            response.message,
                            response.data
                        )
                    )
                    else -> emit(Resource.Error(response.message))
                }
            } catch (e: Exception) {
                emit(Resource.Error(message = e.toString()))
            }
        }
}