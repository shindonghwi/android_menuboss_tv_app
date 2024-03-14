package com.orot.menuboss_tv.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject


enum class Brand(val domain: String) {
    AMAZON("fire-"),
    GOOGLE("ggl-"),
}

class DeviceInfoUtil @Inject constructor() {

    fun getDeviceTotalInformation(context: Context): String {

        val os = getVersion()
        val metric = getDisplayMetrics(context)
        val memory = getAvailableMemory()

        val deviceInfo = StringBuilder()
        .append("\n")
        deviceInfo.append("Device: ").append(Build.DEVICE).append("\n")
        deviceInfo.append("Model: ").append(Build.MODEL).append("\n")
        deviceInfo.append("Product: ").append(Build.PRODUCT).append("\n")
        deviceInfo.append("Brand: ").append(Build.BRAND).append("\n")
        deviceInfo.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n")
        deviceInfo.append("Board: ").append(Build.BOARD).append("\n")
        deviceInfo.append("Hardware: ").append(Build.HARDWARE).append("\n")
        deviceInfo.append("Fingerprint: ").append(Build.FINGERPRINT).append("\n")
        deviceInfo.append("CPU_ABI: ").append(Build.CPU_ABI).append("\n")
        deviceInfo.append("CPU_ABI2: ").append(Build.CPU_ABI2).append("\n")
        deviceInfo.append("OS Version: ").append(os).append("\n")
        deviceInfo.append("Display Metrics: ").append(metric).append("\n")
        deviceInfo.append("Available Memory: ").append(memory).append("\n")
        .append("\n")

        return deviceInfo.toString()
    }

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

    private fun getVersion(): String {
        val release = java.lang.Double.parseDouble(java.lang.String(Build.VERSION.RELEASE).replaceAll("(\\d+[.]\\d+)(.*)", "$1"))
        val codeName: String = if (release >= 4.1 && release < 4.4) "3 Jelly Bean"
        else if (release < 5) "4 Kit Kat"
        else if (release < 6) "5 Lollipop"
        else if (release < 7) "6 Marshmallow"
        else if (release < 8) "7 Nougat"
        else if (release < 9) "8 Oreo"
        else if (release < 10) "9 Pie"
        else if (release < 11) "10 Q"
        else if (release < 12) "11 R"
        else if (release < 13) "12 S"
        else "13 Upper"

        return codeName + " v" + release + ", API Level: " + Build.VERSION.SDK_INT
    }

    fun getDisplayMetrics(context: Context): String {
        val metrics = context.resources.displayMetrics
        return "Width: " + metrics.widthPixels + " Height: " + metrics.heightPixels + " Density: " + metrics.densityDpi + "dpi"
    }

    private fun getAvailableMemory(): String {
        val runtime = Runtime.getRuntime()
        val usedMemInMB =
            (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
        val maxHeapSizeInMB = runtime.maxMemory() / 1048576L
        val availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB
        return "Used Memory: " + usedMemInMB + "MB Max Heap Size: " + maxHeapSizeInMB + "MB Available Heap Size: " + availHeapSizeInMB + "MB"
    }
}