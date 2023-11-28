package com.orot.menuboss_tv.logging.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class FirebaseAnalyticsUtil @Inject constructor(private val analytics: FirebaseAnalytics) {

    companion object {
        private const val TAG = "FirebaseAnalyticsUtil"
    }

    enum class Event(val value: String) {
        GET_DEVICE_INFO("getDeviceInfo"), // 디바이스 정보 조회 API
        GET_DEVICE_PLAYLIST_INFO("getDevicePlaylist"), // 플레이리스트 정보 조회 API
        GET_DEVICE_SCHEDULE_INFO("getDeviceSchedule"), // 스케줄 정보 조회 API
        ERROR("error") // 에러
    }

    fun recordEvent(event: Event, message: HashMap<String, String>) {
        try {
            if (event == Event.GET_DEVICE_INFO) {
                analytics.setUserId(message["uuid"])
            }

            analytics.logEvent("test_event", Bundle().apply {
                putString("testKey", "testValue")
            })

            analytics.logEvent(event.name, Bundle().apply {
                message.forEach { (key, value) ->
                    putString(key, value)
                }
            })
//            DLog.w(TAG, "recordEvent: $event Success")
        } catch (e: Exception) {
            e.printStackTrace()
//            DLog.w(TAG, "recordEvent: $event Fail")
        }
    }
}