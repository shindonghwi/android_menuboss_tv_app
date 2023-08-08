package com.orot.menuboss_tv.data.api

import com.orot.menuboss_tv.data.constants.API_VERSION
import com.orot.menuboss_tv.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TvApi {

    /**
     * @feature: 디ㅣ바이스 정보 조회
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
    ): Response<ApiResponse<Any>>

}