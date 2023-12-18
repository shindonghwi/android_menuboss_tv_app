package com.orot.menuboss_tv.domain.entities
data class ContentInfo(
    val type: Type?,
    val duration: Int?,
    val property: Property?,
    val contentId: String?
){
    data class Type(
        val code: String,
        val name: String,
    )
    data class Property(
        val width: Int?,
        val height: Int?,
        val size: Int?,
        val duration: Int?,
        val rotation: Int?,
        val contentType: String?,
        val codec: String?,
        val imageUrl: String?,
        val videoUrl: String?,
    )
}