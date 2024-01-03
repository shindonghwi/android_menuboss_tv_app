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

class DeviceInfoUtil @Inject constructor(
    private val firebaseAnalyticsUtil: FirebaseAnalyticsUtil
) {
    fun isAmazonDevice(): Boolean {
        return Build.MANUFACTURER.equals("Amazon", ignoreCase = true)
    }

    /** ssaid 값 추출 기능: 공장 초기화전까지 고유한 값 */
    @SuppressLint("HardwareIds")
    fun getAndroidUniqueId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    /** wlan0을 검색 대상으로 잡고 MAC Address 조회. */
    fun getMacAddress(): String {
        var macAddress = ""
        try {
            val all: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.name.equals("wlan0", ignoreCase = true)) continue
                val macBytes = nif.hardwareAddress ?: return ""
                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }
                if (res1.isNotEmpty()) {
                    res1.deleteCharAt(res1.length - 1)
                }
                macAddress = res1.toString()
            }
        } catch (ex: Exception) {
            firebaseAnalyticsUtil.recordEvent(
                FirebaseAnalyticsUtil.Event.ERROR, hashMapOf(
                    "message" to ex.message.toString(), "cause" to ex.cause.toString()
                )
            )
        }

        macAddress.ifEmpty {
            macAddress = getReSearchMacAddress()
        }

        return macAddress
    }

    /** wlan을 검색 대상으로 잡고 MAC Address 조회. 일반적인 경우에는 여기로 오지 않음. */
    private fun getReSearchMacAddress(): String {
        try {
            val all: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.name.startsWith("wlan", ignoreCase = true)) continue
                val macBytes = nif.hardwareAddress ?: return ""
                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }
                if (res1.isNotEmpty()) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (ex: Exception) {
            return ""
        }
        return ""
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