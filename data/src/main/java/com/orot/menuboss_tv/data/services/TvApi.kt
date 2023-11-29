package com.orot.menuboss_tv.data.services

import com.orot.menuboss_tv.domain.constants.API_VERSION
import com.orot.menuboss_tv.domain.entities.ApiResponse
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.DevicePlaylistModel
import com.orot.menuboss_tv.domain.entities.DeviceScheduleModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface TvApi {

    /**
     * @feature: 디바이스 정보 조회
     *
     * @author: 2023/08/08 4:18 PM donghwishin
     */
    @GET("$API_VERSION/screens/connect/{uuid}")
    suspend fun getDeviceInfo(
        @Path(value = "uuid") uuid: String,
    ): Response<ApiResponse<DeviceModel>>

    /**
     * @feature: 플레이리스트 정보 조회
     *
     * @author: 2023/10/03 11:56 AM donghwishin
     */
    @GET("$API_VERSION/screens/connect/{uuid}/playlist")
    suspend fun getDevicePlaylist(
        @Path(value = "uuid") uuid: String,
        @Header("Authorization") authorization: String? = null
    ): Response<ApiResponse<DevicePlaylistModel>>

    /**
     * @feature: 스케줄 정보 조회
     *
     * @author: 2023/10/03 11:56 AM donghwishin
     */
    @GET("$API_VERSION/screens/connect/{uuid}/schedule")
    suspend fun getDeviceSchedule(
        @Path(value = "uuid") uuid: String,
        @Header("Authorization") authorization: String? = null
    ): Response<ApiResponse<DeviceScheduleModel>>

    /**
     * @feature: 스케줄 정보 조회
     *
     * @author: 2023/11/29 9:38 AM donghwishin
     */
    @POST("$API_VERSION/screens/connect/{oldUuid}/to/{newUuid}")
    suspend fun updateUuid(
        @Path(value = "oldUuid") oldUuid: String,
        @Path(value = "newUuid") newUuid: String,
    ): Response<ApiResponse<Unit?>>

}