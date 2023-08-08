package com.orot.menuboss_tv.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.rive.runtime.kotlin.core.Loop
import com.orot.menuboss_tv.presentation.R
import com.orot.menuboss_tv.ui.components.RiveAnimation
import com.orot.menuboss_tv.ui.navigations.LocalNavController
import com.orot.menuboss_tv.ui.navigations.RouteScreen
import com.orot.menuboss_tv.ui.theme.colorBackground
import com.orot.menuboss_tv.utils.coroutineScopeOnMain

@Composable
fun SplashScreen() {
    val navController = LocalNavController.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorBackground),
        contentAlignment = Alignment.Center
    ) {
        RiveAnimation(
            modifier = Modifier.size(300.dp),
            animation = R.raw.logo,
            onInit = {
                it.play(loop = Loop.ONESHOT)
            },
            onAnimEnd = {
                coroutineScopeOnMain(initDelay = 1000) { navController.navigate(RouteScreen.AuthScreen.route) }
            },
        )
    }
}

