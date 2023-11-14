package com.orot.menuboss_tv.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import app.rive.runtime.kotlin.core.Loop
import com.orot.menuboss_tv.logging.datadog.DataDogLoggingUtil
import com.orot.menuboss_tv.presentation.R
import com.orot.menuboss_tv.ui.components.RiveAnimation
import com.orot.menuboss_tv.ui.navigations.LocalNavController
import com.orot.menuboss_tv.ui.navigations.RouteScreen
import com.orot.menuboss_tv.ui.theme.colorBackground

/**
 * @description{
 *   TODO: 스플래시 화면에서 에러 처리나 로딩중일때 화면에 표시 해야 할 것들이 있음.
 * }
*/

@Composable
fun SplashScreen(
    uuid: String,
    splashViewModel: SplashViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current

    val doAuthScreenActionState = splashViewModel.navigateToAuthState.collectAsState().value
    val doMenuScreenActionState = splashViewModel.navigateToMenuState.collectAsState().value

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                DataDogLoggingUtil.startView(
                    RouteScreen.SplashScreen.route, "${RouteScreen.SplashScreen}"
                )
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                DataDogLoggingUtil.stopView(RouteScreen.SplashScreen.route)
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    /**
     * @feature: 디바이스 정보 요청
     * @author: 2023/10/15 12:52 PM donghwishin
     */
    LaunchedEffect(key1 = Unit, block = {
        splashViewModel.requestGetDeviceInfo(uuid = uuid)
    })

    /**
     * @feature: 인증화면으로 이동하는 기능
     * @author: 2023/10/12 1:06 PM donghwishin
     */
    DisposableEffect(key1 = doAuthScreenActionState, effect = {
        if (doAuthScreenActionState) {
            navController.navigate(RouteScreen.AuthScreen.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
        onDispose {
            splashViewModel.triggerAuthState(false)
        }
    })

    /**
     * @feature: 메뉴판 화면으로 이동하는 기능
     * @author: 2023/10/12 1:06 PM donghwishin
     */
    DisposableEffect(key1 = doMenuScreenActionState, effect = {
        if (doMenuScreenActionState) {
            navController.navigate(RouteScreen.MenuBoardScreen.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
        onDispose {
            splashViewModel.triggerMenuState(false)
        }
    })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorBackground),
        contentAlignment = Alignment.Center
    ) {
        RiveAnimation(
            animation = R.raw.logo,
            onInit = {
                it.play(loop = Loop.ONESHOT)
            },
            onAnimEnd = {},
        )
    }
}

