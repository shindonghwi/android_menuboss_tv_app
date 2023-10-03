package com.orot.menuboss_tv.domain.entities

data class DeviceScheduleModel(
    val `object`: String?,
    val scheduleId: Int?,
    val name: String?,
    val timeline: List<Timeline>?,
    val updatedAt: String?,
) {
    data class Timeline(
        val time: TimeInfo?,
        val playlist: Playlist?,
        val property: DeviceProperty?,
        val contents: List<ContentInfo>?,
    ) {
        data class TimeInfo(
            val start: String,
            val end: String,
        )

        data class Playlist(
            val `object`: String,
            val name: String,
            val property: DeviceProperty,
        )
    }
}


