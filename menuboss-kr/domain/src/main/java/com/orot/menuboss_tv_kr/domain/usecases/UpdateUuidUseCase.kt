package com.orot.menuboss_tv_kr.domain.usecases

import com.orot.menuboss_tv_kr.domain.entities.Resource
import com.orot.menuboss_tv_kr.domain.repository.TvRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateUuidUseCase @Inject constructor(private val tvRepository: TvRepository) {
    suspend operator fun invoke(oldUuid: String, newUuid: String): Flow<Resource<Unit?>> =
        flow {
            emit(Resource.Loading())
            try {
                val response = tvRepository.updateUuid(oldUuid, newUuid)
                emit(Resource.Success(response.message, response.data))
            } catch (e: Exception) {
                emit(Resource.Error(message = e.toString()))
            }
        }
}