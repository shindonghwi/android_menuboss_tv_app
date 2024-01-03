package com.orot.menuboss_tv_kr.ui.model

import com.orot.menuboss_tv_kr.domain.entities.DevicePlaylistModel
import com.orot.menuboss_tv_kr.domain.entities.DeviceProperty
import com.orot.menuboss_tv_kr.domain.entities.DeviceScheduleModel

data class SimpleScreenModel(
    val isExpired: Boolean? = null,
    val isDeleted: Boolean? = null,
    val isPlaylist: Boolean? = null,
    val playlistModel: DevicePlaylistModel? = null,
    val scheduleModel: DeviceScheduleModel? = null,
)
