package com.orot.menuboss_tv.data.repository

import com.orot.menuboss_tv.data.services.TvApi
import com.orot.menuboss_tv.data.utils.SafeApiRequest
import com.orot.menuboss_tv.domain.entities.ApiResponse
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.DevicePlaylistModel
import com.orot.menuboss_tv.domain.entities.DeviceScheduleModel
import com.orot.menuboss_tv.domain.repository.TvRepository
import javax.inject.Inject

class TvRepositoryImpl @Inject constructor(
    private val tvApi: TvApi,
) : TvRepository, SafeApiRequest() {
    override suspend fun getDeviceInfo(uuid: String): ApiResponse<DeviceModel> {
        val response = safeApiRequest { tvApi.getDeviceInfo(uuid) }
        return ApiResponse(
            status = response.status,
            message = response.message,
            data = response.data
        )
    }

    override suspend fun getDevicePlaylist(uuid: String, accessToken: String): ApiResponse<DevicePlaylistModel> {
        val response = safeApiRequest { tvApi.getDevicePlaylist(uuid, "Bearer $accessToken") }
        return ApiResponse(
            status = response.status,
            message = response.message,
            data = response.data
        )
    }

    override suspend fun getDeviceSchedule(uuid: String, accessToken: String): ApiResponse<DeviceScheduleModel> {
        val response = safeApiRequest { tvApi.getDeviceSchedule(uuid, "Bearer $accessToken") }
        return ApiResponse(
            status = response.status,
            message = response.message,
            data = response.data
        )
    }
}