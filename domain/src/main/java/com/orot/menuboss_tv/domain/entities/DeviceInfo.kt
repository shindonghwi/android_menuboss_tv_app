package com.orot.menuboss_tv.domain.entities

data class DeviceInfo(
    val status: String,
    val tv: TV,
    val business: Business?,
    val product: Product?,
    val hasPlan: Boolean
){
    data class TV(
        val name: String,

        // TV 등록이 된 경우
        val accessToken: String?,
        val screenUrl: String?,

        // TV 등록이 안된 경우
        val code: String?,
        val qrUrl: String?
    )

    data class Business(
        val name: String,
    )

    data class Product(
        val title: String,
    )
}


