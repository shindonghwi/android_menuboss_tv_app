package com.orot.menuboss_tv.utils

import android.content.ContentResolver
import android.provider.Settings
import java.net.NetworkInterface
import java.util.Collections


object DeviceInfo {

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
        }

        macAddress.ifEmpty {
            macAddress = getReSearchMacAddress()
        }

        return if (isValidMacAddress(macAddress)){
            macAddress
        }else{
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
        return pattern.matches(macAddress)
    }


}