package com.orot.menuboss_tv.data.repository

import android.util.Log
import com.orot.menuboss_tv.data.api.TvApi
import com.orot.menuboss_tv.data.mapper.DeviceInfoMapper
import com.orot.menuboss_tv.data.utils.SafeApiRequest
import com.orot.menuboss_tv.domain.entities.ApiResponse
import com.orot.menuboss_tv.domain.entities.DeviceInfo
import com.orot.menuboss_tv.domain.repository.TvRepository
import javax.inject.Inject

class TvRepositoryImpl @Inject constructor(
    private val tvApi: TvApi,
    private val mapper: DeviceInfoMapper
) : TvRepository, SafeApiRequest() {
    override suspend fun getDeviceInfo(uuid: String): ApiResponse<DeviceInfo> {
        val response = safeApiRequest { tvApi.getDeviceInfo(uuid) }
        return ApiResponse(
            status = response.status,
            message = response.message,
            data = response.data?.let { mapper.mapFromDTO(it) }
        )
    }
}