package com.orot.menuboss_tv


import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.core.view.WindowCompat
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.amazon.A3L.messaging.A3LMessaging
import com.google.android.gms.tasks.Task
import com.orot.menuboss_tv.ui.navigations.Navigation
import com.orot.menuboss_tv.ui.theme.MenuBossTVTheme
import java.util.Objects


@OptIn(ExperimentalTvMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        StrictMode.setThreadPolicy(ThreadPolicy.Builder().permitAll().build())

        A3LMessaging.getToken()
            .addOnCompleteListener { task: Task<String> ->
                Log.w(TAG, "onComplete: ", task.exception)
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM/ADM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.w(TAG, "onComplete: token :$token")
                Log.d(TAG, token)
            }

        val currentPlatform = A3LMessaging.getCurrentPlatform(applicationContext)
        Log.w("Asdadssdasda", "currentPlatform ${currentPlatform}")
        Log.w("Asdadssdasda", "BUILD_TYPE ${com.amazon.A3L.messaging.BuildConfig.BUILD_TYPE}")

        Log.w("Asdadssdasda", "serialNumber ${Build.MANUFACTURER}")
        Log.w("zxcjkzhxck;lzxcjk",Build.MANUFACTURER + " " + Build.MODEL + " " + Build.DEVICE+ " " + Build.VERSION.INCREMENTAL + " " + Build.SERIAL);
        Log.w("zxcjkzhxck;lzxcjk",Build.PRODUCT + " " + Build.BRAND + " " + Build.HARDWARE+ " " + Build.VERSION.RELEASE + " " + Build.VERSION.SDK_INT);
        Log.w("zxcjkzhxck;lzxcjk",Build.FINGERPRINT);

        val android_id: String = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        Log.w("zxcjkzhxck;lzxcjk", "android_id : ${android_id}")

        val dd = Objects.hash(android_id, Build.FINGERPRINT)
        Log.w("zxcjkzhxck;lzxcjk", "dd : ${dd}")



        setContent {
            MenuBossTVTheme {
                Surface(
                    shape = RectangleShape,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Navigation()
                }
            }
        }

    }

    companion object {
        const val TAG = "SampleA3LMainActivity"
    }
}