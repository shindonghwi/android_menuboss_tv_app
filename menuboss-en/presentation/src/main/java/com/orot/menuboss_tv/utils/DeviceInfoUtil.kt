package com.orot.menuboss_tv.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.orot.menuboss_tv.logging.firebase.FirebaseAnalyticsUtil
import java.net.NetworkInterface
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.Collections
import java.util.UUID
import javax.inject.Inject

enum class Brand(val domain: String) {
    AMAZON("fire-"),
    GOOGLE("ggl-"),
}

class DeviceInfoUtil @Inject constructor() {
    fun isAmazonDevice(): Boolean {
        return Build.MANUFACTURER.equals("Amazon", ignoreCase = true)
    }

    /** ssaid 값 추출 기능: 공장 초기화전까지 고유한 값 */
    @SuppressLint("HardwareIds")
    fun getAndroidUniqueId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    /**
     * @feature: MAC 주소에 기반한 고유한 UUID 생성하기
     * @author: 2023/08/09 4:16 PM donghwishin
     */
    fun generateUniqueUUID(uniqueValue: String, data: String): UUID {
        val combinedString = uniqueValue + data
        val sha256 = MessageDigest.getInstance("SHA-256")
        val hash = sha256.digest(combinedString.toByteArray())

        val buffer = ByteBuffer.wrap(hash)
        val high = buffer.long
        val low = buffer.long
        return UUID(high, low)
    }
}