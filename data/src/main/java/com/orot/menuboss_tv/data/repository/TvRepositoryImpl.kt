package com.orot.menuboss_tv.data.repository

import com.orot.menuboss_tv.data.api.TvApi
import com.orot.menuboss_tv.data.model.ApiResponse
import com.orot.menuboss_tv.data.utils.SafeApiRequest
import com.orot.menuboss_tv.domain.repository.TvRepository
import javax.inject.Inject

class TvRepositoryImpl @Inject constructor(
    private val tvApi: TvApi
) : TvRepository, SafeApiRequest() {
    override suspend fun getDeviceInfo(uuid: String): ApiResponse<Any> {
        val response = safeApiRequest { tvApi.getDeviceInfo(uuid) }
        return ApiResponse(
            status = response.status, message = response.message, data = response.data
        )
    }
}