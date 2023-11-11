package com.orot.menuboss_tv.domain.entities

data class DevicePlaylistModel(
    val `object`: String?,
    val name: String?,
    val property: DeviceProperty?,
    val contents: List<ContentInfo>?,
)

