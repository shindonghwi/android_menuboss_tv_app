package com.orot.menuboss_tv_kr.domain.repository

import com.orot.menuboss_tv_kr.domain.entities.ApiResponse
import com.orot.menuboss_tv_kr.domain.entities.DeviceModel
import com.orot.menuboss_tv_kr.domain.entities.DevicePlaylistModel
import com.orot.menuboss_tv_kr.domain.entities.DeviceScheduleModel

interface TvRepository {

    suspend fun getDeviceInfo(uuid: String): ApiResponse<DeviceModel>

    suspend fun getDevicePlaylist(uuid: String, accessToken: String): ApiResponse<DevicePlaylistModel>

    suspend fun getDeviceSchedule(uuid: String, accessToken: String): ApiResponse<DeviceScheduleModel>

    suspend fun updateUuid(oldUuid: String, newUuid: String): ApiResponse<Unit?>
}