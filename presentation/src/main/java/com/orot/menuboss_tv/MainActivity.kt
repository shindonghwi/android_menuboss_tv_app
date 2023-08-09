package com.orot.menuboss_tv


import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.orot.menuboss_tv.ui.navigations.Navigation
import com.orot.menuboss_tv.ui.theme.MenuBossTVTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) // 화면이 안꺼지게 방지

        setContent {
            MenuBossTVTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Navigation()
                }
            }
        }
    }
}