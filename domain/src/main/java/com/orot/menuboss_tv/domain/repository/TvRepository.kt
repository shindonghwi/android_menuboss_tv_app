package com.orot.menuboss_tv.domain.repository

import com.orot.menuboss_tv.domain.entities.ApiResponse
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.DevicePlaylistModel
import com.orot.menuboss_tv.domain.entities.DeviceScheduleModel

interface TvRepository {

    suspend fun getDeviceInfo(uuid: String): ApiResponse<DeviceModel>

    suspend fun getDevicePlaylist(uuid: String, accessToken: String): ApiResponse<DevicePlaylistModel>

    suspend fun getDeviceSchedule(uuid: String, accessToken: String): ApiResponse<DeviceScheduleModel>

    suspend fun updateUuid(oldUuid: String, newUuid: String): ApiResponse<Unit?>
}