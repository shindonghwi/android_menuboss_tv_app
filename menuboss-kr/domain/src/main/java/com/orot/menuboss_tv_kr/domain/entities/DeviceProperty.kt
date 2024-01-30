package com.orot.menuboss_tv_kr.domain.entities
data class DeviceProperty(
    val direction: PropertyDirection?,
    val fill: PropertyFill?,
){
    data class PropertyDirection(
        val code: String,
        val name: String,
    )
    data class PropertyFill(
        val code: String,
        val name: String,
    )
}