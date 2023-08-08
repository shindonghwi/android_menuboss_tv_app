package com.orot.menuboss_tv.domain.repository

import com.orot.menuboss_tv.data.model.ApiResponse

interface TvRepository {

    suspend fun getDeviceInfo(uuid: String): ApiResponse<Any>

}