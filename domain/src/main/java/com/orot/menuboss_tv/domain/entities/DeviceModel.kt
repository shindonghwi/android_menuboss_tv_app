package com.orot.menuboss_tv.domain.entities

data class DeviceModel(
    val `object`: String?,
    val status: String,
    val property: Property?,
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
        val grpc: Grpc?,
    )

    data class Grpc(
        val host: String,
        val port: String,
    )
}


