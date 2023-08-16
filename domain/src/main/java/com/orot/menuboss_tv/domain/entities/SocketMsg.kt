package com.orot.menuboss_tv.domain.entities

data class SocketMsg(
    val command: String?,
    val `object`: String?,
    val pages: List<Pages>
) {
    data class Pages(
        val conversionTime: Int,
        val imageUrl: String,
    )
}

