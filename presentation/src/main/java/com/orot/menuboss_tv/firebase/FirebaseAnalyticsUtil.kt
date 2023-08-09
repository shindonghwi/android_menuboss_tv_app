package com.orot.menuboss_tv.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class FirebaseAnalyticsUtil @Inject constructor(private val analytics: FirebaseAnalytics) {

    enum class Event(val value: String) {
        GET_DEVICE_INFO("getDeviceInfo"), // 디바이스 정보 조회 API
        CREATE_FAIL_UUID("createFailUUID"), // UUID 생성 실패
        NOT_FOUND_MAC_ADDRESS("notFoundMacAddress"), // MAC Address 조회 실패
        ERROR("error") // 에러
    }

    fun recordEvent(event: Event, message: HashMap<String, String>) {
        try {
            if (event == Event.GET_DEVICE_INFO){
                analytics.setUserId(message["uuid"])
            }

            analytics.logEvent(event.name, Bundle().apply {
                message.forEach { (key, value) ->
                    putString(key, value)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}