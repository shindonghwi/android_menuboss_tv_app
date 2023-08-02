package com.orot.menuboss_tv

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.core.view.WindowCompat
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.amazon.A3L.messaging.A3LMessaging
import com.google.android.gms.tasks.Task
import com.orot.menuboss_tv.ui.theme.MenuBossTVTheme

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

        setContent {
            MenuBossTVTheme {
                Surface(
                    shape = RectangleShape,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    App()
                }
            }
        }

    }

    companion object {
        const val TAG = "SampleA3LMainActivity"
    }
}