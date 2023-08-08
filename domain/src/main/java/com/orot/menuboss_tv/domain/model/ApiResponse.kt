package com.orot.menuboss_tv.data.model

data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T?
)

sealed class Resource<T>(
    val message: String? = null,
    val data: T? = null
) {
    class Loading<T> : Resource<T>()
    class Success<T>(message: String?, data: T?) : Resource<T>(message = message, data = data)
    class Error<T>(message: String?) : Resource<T>(message = message)
}