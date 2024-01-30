package com.orot.menuboss_tv_kr.data.repository

import android.util.Log
import com.orot.menuboss_tv_kr.data.services.TvApi
import com.orot.menuboss_tv_kr.data.utils.SafeApiRequest
import com.orot.menuboss_tv_kr.domain.entities.ApiResponse
import com.orot.menuboss_tv_kr.domain.entities.DeviceModel
import com.orot.menuboss_tv_kr.domain.entities.DevicePlaylistModel
import com.orot.menuboss_tv_kr.domain.entities.DeviceScheduleModel
import com.orot.menuboss_tv_kr.domain.repository.TvRepository
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

    override suspend fun updateUuid(oldUuid: String, newUuid: String): ApiResponse<Unit?> {
        val response = safeApiRequest { tvApi.updateUuid(oldUuid, newUuid) }
        return ApiResponse(
            status = response.status,
            message = response.message,
            data = response.data
        )
    }
}