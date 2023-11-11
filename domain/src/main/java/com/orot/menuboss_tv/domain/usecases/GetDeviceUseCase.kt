package com.orot.menuboss_tv.domain.usecases

import com.datadog.android.rum.GlobalRumMonitor
import com.datadog.android.rum.RumErrorSource
import com.datadog.android.rum.RumResourceKind
import com.datadog.android.rum.RumResourceMethod
import com.orot.menuboss_tv.domain.constants.API_VERSION
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.repository.TvRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDeviceUseCase @Inject constructor(private val tvRepository: TvRepository) {
    suspend operator fun invoke(uuid: String): Flow<Resource<DeviceModel>> =
        flow {
            emit(Resource.Loading())
            try {
                startResource(uuid)
                val response = tvRepository.getDeviceInfo(uuid)
                stopResource(response.status)
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
                stopResourceWithError(e.message.toString(), e)
                emit(Resource.Error(message = e.toString()))
            }
        }

    private fun startResource(uuid: String) {
        GlobalRumMonitor.get()
            .startResource(
                "API - getDeviceInfo",
                RumResourceMethod.GET,
                "$API_VERSION/screens/connect/$uuid"
            )
    }

    private fun stopResource(status: Int) {
        GlobalRumMonitor.get()
            .stopResource(
                "API - getDeviceInfo",
                status,
                size = null,
                kind = RumResourceKind.FETCH,
                attributes = emptyMap(),
            )
    }

    private fun stopResourceWithError(message: String, e: Exception) {
        GlobalRumMonitor.get()
            .stopResourceWithError(
                "API - getDeviceInfo",
                statusCode = 500,
                message = message,
                throwable = e,
                source = RumErrorSource.NETWORK,
            )
    }
}