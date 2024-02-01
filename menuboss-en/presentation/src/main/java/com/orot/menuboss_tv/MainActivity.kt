package com.orot.menuboss_tv


import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.orot.menuboss_tv.domain.usecases.remote.UnSubscribeStreamUseCase
import com.orot.menuboss_tv.ui.navigations.Navigation
import com.orot.menuboss_tv.ui.theme.MenuBossTVTheme
import com.orot.menuboss_tv.utils.DeviceInfoUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var deviceInfoUtil: DeviceInfoUtil

    @Inject
    lateinit var unSubscribeStreamUseCase: UnSubscribeStreamUseCase

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) // 화면이 안꺼지게 방지

        setContent {
            MenuBossTVTheme {
                Navigation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onPause() {
        super.onPause()
        window.decorView.setOnSystemUiVisibilityChangeListener(null)
    }

    @Suppress("DEPRECATION")
    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            controller?.apply {
                hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )

            window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    hideSystemUI()
                }
            }
        }
    }
}