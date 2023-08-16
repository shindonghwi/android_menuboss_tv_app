package com.orot.menuboss_tv.data.services

import com.orot.menuboss_tv.data.constants.API_VERSION
import com.orot.menuboss_tv.data.models.DeviceInfoDTO
import com.orot.menuboss_tv.domain.entities.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TvApi {

    /**
     * @feature: 디바이스 정보 조회
     *
     * @author: 2023/08/08 4:18 PM donghwishin
     *
     * @description{
     *
     * }
     */
    @GET("$API_VERSION/devices/{uuid}")
    suspend fun getDeviceInfo(
        @Path(value = "uuid") uuid: String,
    ): Response<ApiResponse<DeviceInfoDTO>>

}