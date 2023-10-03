package com.orot.menuboss_tv


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.orot.menuboss_tv.ui.navigations.Navigation
import com.orot.menuboss_tv.ui.theme.MenuBossTVTheme
import com.orot.menuboss_tv.utils.DeviceInfoUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var deviceInfoUtil: DeviceInfoUtil
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) // 화면이 안꺼지게 방지

        setContent {
            MenuBossTVTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Navigation(uuidValue = getXUniqueId())
                }
            }
        }
    }

    /**
     * @feature: 디바이스의 고유한 식별자를 생성합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    private fun getXUniqueId(): String {
        return deviceInfoUtil.run {
            val uuid1 = generateUniqueUUID(
                getMacAddress(),
                "${Build.PRODUCT}${Build.BRAND}${Build.HARDWARE}"
            )
            val uuid2 = generateUniqueUUID(
                getMacAddress(),
                "${Build.MANUFACTURER}${Build.MODEL}${Build.DEVICE}"
            )
            val uuid3 =
                generateUniqueUUID(getMacAddress(), Build.FINGERPRINT)
            val uuid = generateUniqueUUID(
                uuid1.toString(),
                "$uuid2$uuid3"
            ).toString()
            Log.w(TAG, "getXUniqueId: $uuid")
            return@run uuid
        }
    }
}