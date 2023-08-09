package com.orot.menuboss_tv.domain.repository

import com.orot.menuboss_tv.domain.entities.ApiResponse
import com.orot.menuboss_tv.domain.entities.DeviceInfo

interface TvRepository {

    suspend fun getDeviceInfo(uuid: String): ApiResponse<DeviceInfo>

}