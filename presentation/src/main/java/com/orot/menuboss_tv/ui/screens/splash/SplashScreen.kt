package com.orot.menuboss_tv.ui.screens.splash

import android.util.Log
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.controllers.RiveFileController
import app.rive.runtime.kotlin.core.Fit
import app.rive.runtime.kotlin.core.Loop
import app.rive.runtime.kotlin.core.PlayableInstance
import com.orot.menuboss_tv.presentation.R
import com.orot.menuboss_tv.ui.navigations.LocalNavController
import com.orot.menuboss_tv.ui.theme.colorBackground
import com.orot.menuboss_tv.utils.coroutineScopeOnMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen() {
    val navController = LocalNavController.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorBackground),
        contentAlignment = Alignment.Center
    ) {
        ComposableRiveAnimationView(
            modifier = Modifier.size(300.dp),
            animation = R.raw.logo,
            onInit = {
                it.play(loop = Loop.ONESHOT)
            },
            onAnimEnd = {
                coroutineScopeOnMain(initDelay = 1000) { navController.navigate("/auth") }
            },
        )
    }
}


@Composable
fun ComposableRiveAnimationView(
    modifier: Modifier = Modifier,
    @RawRes animation: Int,
    stateMachineName: String? = null,
    alignment: app.rive.runtime.kotlin.core.Alignment = app.rive.runtime.kotlin.core.Alignment.CENTER,
    fit: Fit = app.rive.runtime.kotlin.core.Fit.CONTAIN,
    onInit: (RiveAnimationView) -> Unit,
    onAnimEnd: () -> Unit
) {
    AndroidView(modifier = modifier, factory = { context ->
        RiveAnimationView(context).also {
            it.setRiveResource(
                resId = animation,
                stateMachineName = stateMachineName,
                alignment = alignment,
                fit = fit,
                autoplay = false
            )
            it.registerListener(
                object : RiveFileController.Listener {
                    override fun notifyLoop(animation: PlayableInstance) {}
                    override fun notifyPlay(animation: PlayableInstance) {}
                    override fun notifyStateChanged(stateMachineName: String, stateName: String) {}
                    override fun notifyStop(animation: PlayableInstance) {}
                    override fun notifyPause(animation: PlayableInstance) = onAnimEnd.invoke()
                },
            )
        }
    }, update = { view -> onInit(view) })

}