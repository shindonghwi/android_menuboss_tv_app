package com.orot.menuboss_tv_kr.ui.components

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.controllers.RiveFileController
import app.rive.runtime.kotlin.core.Fit
import app.rive.runtime.kotlin.core.PlayableInstance


@Composable
fun RiveAnimation(
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