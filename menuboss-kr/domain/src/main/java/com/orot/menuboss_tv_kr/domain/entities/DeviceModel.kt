package com.orot.menuboss_tv_kr.domain.entities

data class DeviceModel(
    val `object`: String?,
    val status: String,
    val property: Property?,
    val linkProfile: LinkProfile?,
    val playing: Playing?,
) {
    data class LinkProfile(
        val pinCode: String?,
        val qrUrl: String?,
    )

    data class Property(
        val version: String?,
        val name: String?,
        val accessToken: String?,
        val screenUrl: String?,
        val grpc: Grpc?,
    )

    data class Grpc(
        val host: String,
        val port: String,
    )

    data class Playing(
        val contentType: String?,
    )
}


