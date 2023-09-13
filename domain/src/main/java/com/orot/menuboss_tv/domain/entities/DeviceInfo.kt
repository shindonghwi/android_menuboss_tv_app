package com.orot.menuboss_tv.domain.entities

data class DeviceInfo(
    val status: String,
    val property: Property?,
    val `object`: String?,
    val linkProfile: LinkProfile?,
) {
    data class LinkProfile(
        val pinCode: String?,
        val qrUrl: String?,
    )

    data class Property(
        val name: String?,
        val accessToken: String?,
        val screenUrl: String?,
    )
}


