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
import androidx.hilt.navigation.compose.hiltViewModel
import app.rive.runtime.kotlin.core.Loop
import com.orot.menuboss_tv.presentation.R
import com.orot.menuboss_tv.ui.components.RiveAnimation
import com.orot.menuboss_tv.ui.model.UiState
import com.orot.menuboss_tv.ui.navigations.LocalNavController
import com.orot.menuboss_tv.ui.navigations.RouteScreen
import com.orot.menuboss_tv.ui.theme.colorBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    uuid: String,
    splashViewModel: SplashViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current

    val deviceState = splashViewModel.deviceState.collectAsState().value
    val doAuthScreenActionState = splashViewModel.navigateToAuthState.collectAsState().value
    val doMenuScreenActionState = splashViewModel.navigateToMenuState.collectAsState().value

    /**
     * @feature: 디바이스 정보 요청
     * @author: 2023/10/15 12:52 PM donghwishin
     */
    DisposableEffect(key1 = Unit, effect = {
        splashViewModel.run {
            // 디바이스 정보 요청
            CoroutineScope(Dispatchers.Main).launch { requestGetDeviceInfo(uuid = uuid) }

            // 뷰 모델 상태 초기화
            onDispose { initState() }
        }
    })

    /**
     * @feature: 인증화면으로 이동하는 기능
     * @author: 2023/10/12 1:06 PM donghwishin
     */
    LaunchedEffect(doAuthScreenActionState) {
        if (doAuthScreenActionState) {
            navController.navigate(RouteScreen.AuthScreen.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
    }

    /**
     * @feature: 메뉴판 화면으로 이동하는 기능
     * @author: 2023/10/12 1:06 PM donghwishin
     */
    LaunchedEffect(doMenuScreenActionState) {
        if (doMenuScreenActionState) {
            navController.navigate(RouteScreen.MenuBoardScreen.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
    }

    /**
     * @feature: 디바이스 정보 응답 관찰
     * @author: 2023/10/15 12:52 PM donghwishin
     *
     * @description{
     *
     *  1. 디바이스 정보가 정상적으로 응답되면,
     *      1-1. 디바이스 상태가 Unlinked이면, 인증화면으로 이동합니다.
     *      1-2. 디바이스 상태가 Linked이면, 메뉴판 화면으로 이동합니다.
     *
     *  2. 디바이스 정보가 정상적으로 응답되지 않으면, 디바이스 정보를 다시 요청합니다.
     * }
     */
    LaunchedEffect(key1 = Unit, block = {
        splashViewModel.run {
            if (deviceState is UiState.Success) {
                if (deviceState.data?.status == "Unlinked") {
                    triggerAuthState(true)
                } else if (deviceState.data?.status == "Linked") {
                    triggerMenuState(true)
                }
            } else if (deviceState is UiState.Error) {
                requestGetDeviceInfo(uuid = uuid)
            }
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

