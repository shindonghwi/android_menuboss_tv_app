package com.orot.menuboss_tv.ui.model

import com.orot.menuboss_tv.domain.entities.DevicePlaylistModel
import com.orot.menuboss_tv.domain.entities.DeviceProperty
import com.orot.menuboss_tv.domain.entities.DeviceScheduleModel

data class SimpleScreenModel(
    val isPlaylist: Boolean? = null,
    val playlistModel: DevicePlaylistModel? = null,
    val scheduleModel: DeviceScheduleModel? = null,
){
    fun copyWith(
        isPlaylist: Boolean? = this.isPlaylist,
        playlistModel: DevicePlaylistModel? = this.playlistModel,
        scheduleModel: DeviceScheduleModel? = this.scheduleModel,
    ): SimpleScreenModel {
        return SimpleScreenModel(
            isPlaylist = isPlaylist,
            playlistModel = playlistModel,
            scheduleModel = scheduleModel
        )
    }
}
