package com.orot.menuboss_tv.utils

import com.orot.menuboss_tv.firebase.FirebaseAnalyticsUtil
import java.net.NetworkInterface
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.Collections
import java.util.UUID
import javax.inject.Inject


class DeviceInfoUtil @Inject constructor(
    private val firebaseAnalyticsUtil: FirebaseAnalyticsUtil
) {

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

        return if (isValidMacAddress(macAddress)) {
            macAddress
        } else {
            firebaseAnalyticsUtil.recordEvent(
                FirebaseAnalyticsUtil.Event.NOT_FOUND_MAC_ADDRESS, hashMapOf(
                    "macAddress" to "wlan not found",
                )
            )
            ""
        }
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

    private fun isValidMacAddress(macAddress: String): Boolean {
        val pattern = Regex("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")
        val isValid = pattern.matches(macAddress)

        if (!isValid) {
            firebaseAnalyticsUtil.recordEvent(
                FirebaseAnalyticsUtil.Event.CREATE_FAIL_UUID, hashMapOf(
                    "macAddress" to macAddress,
                )
            )
        }

        return isValid
    }

    /**
     * @feature: MAC 주소에 기반한 고유한 UUID 생성하기
     * @author: 2023/08/09 4:16 PM donghwishin
     */
    fun generateUniqueUUID(macAddress: String, data: String): UUID {
        val combinedString = macAddress + data
        val sha256 = MessageDigest.getInstance("SHA-256")
        val hash = sha256.digest(combinedString.toByteArray())

        val buffer = ByteBuffer.wrap(hash)
        val high = buffer.long
        val low = buffer.long
        return UUID(high, low)
    }
}