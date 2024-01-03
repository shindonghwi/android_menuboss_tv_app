package com.orot.menuboss_tv_kr.data.utils

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

abstract class SafeApiRequest {

    suspend fun <T : Any> safeApiRequest(call: suspend () -> Response<T>): T {
        val response = call.invoke()
        if (response.isSuccessful) {
            Log.d("SafeApiRequest", "safeApiRequest: ${response.body()}")
            return response.body()!!
        } else {
            val responseError = response.errorBody()?.string()
            val message = StringBuilder()
            responseError?.let {
                try {
                    message.append(JSONObject(it).getString("error"))
                } catch (e: JSONException) {
                    Log.d("SafeApiRequest", "safeApiRequest: error $e")
                }
            }
            Log.d("SafeApiRequest", "safeApiRequest: $message")
            throw ApiException(message.toString())
        }
    }
}