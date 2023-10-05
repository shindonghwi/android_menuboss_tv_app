package com.orot.menuboss_tv.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import app.rive.runtime.kotlin.core.Loop
import com.orot.menuboss_tv.presentation.R
import com.orot.menuboss_tv.ui.components.RiveAnimation
import com.orot.menuboss_tv.ui.model.UiState
import com.orot.menuboss_tv.ui.navigations.LocalMainViewModel
import com.orot.menuboss_tv.ui.navigations.LocalNavController
import com.orot.menuboss_tv.ui.navigations.RouteScreen
import com.orot.menuboss_tv.ui.theme.colorBackground
import com.orot.menuboss_tv.utils.coroutineScopeOnDefault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.URLEncoder

@Composable
fun SplashScreen(
    splashViewModel: SplashViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val mainViewModel = LocalMainViewModel.current
    val deviceState = splashViewModel.deviceState.collectAsState().value

    /**
     * @feature: 스플래시 화면에서 최초로 디바이스 정보를 가져옵니다.
     * @author: 2023/10/02 6:07 PM donghwishin
     * @description{
     *   여기서 디바이스 정보를 가져오는 이유는,
     *   디바이스의 상태가 연결되었는지를 판단한다.
     * }
     */
    LaunchedEffect(key1 = Unit, block = {
        splashViewModel.run {
            requestGetDeviceInfo(mainViewModel.uuid)
        }
    })

    /**
     * @feature: 스플래시 화면에서 디바이스 정보를 가져온 후, 상태에 따라서 다음 화면으로 이동합니다.
     *
     * @author: 2023/10/02 6:08 PM donghwishin
     *
     * @description{
     *   1. 디바이스가 연결이 안되어있는 경우
     *       * Grpc Connect Stream을 구독합니다.
     *       * 조회한 디바이스 정보로 부터 code, qrUrl 정보를 가지고 AuthScreen 으로 이동.
     *
     *   2. 디바이스가 연결 되어있는 경우
     *       * Grpc Content Stream을 구독합니다.
     *       * 조회한 디바이스 정보로 부터 accessToken 정보를 가지고 MenuBoardScreen 으로 이동.
     * }
     */
    LaunchedEffect(deviceState) {
        if (deviceState is UiState.Success) {
            if (deviceState.data?.status == "Unlinked") {
                mainViewModel.run { subscribeConnectStream() }

                val code = deviceState.data.linkProfile?.pinCode ?: ""
                val qrUrl = deviceState.data.linkProfile?.qrUrl ?: ""
                val encodedQrUrl = withContext(Dispatchers.IO) {
                    URLEncoder.encode(qrUrl, "UTF-8")
                }

                navController.navigate("${RouteScreen.AuthScreen.route}/$code/$encodedQrUrl") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            } else if (deviceState.data?.status == "Linked") {
                mainViewModel.run {
                    subscribeContentStream(deviceState.data.property?.accessToken.toString())
                }
                navController.navigate(RouteScreen.MenuBoardScreen.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        } else if (deviceState is UiState.Error) {
            splashViewModel.run {
                requestGetDeviceInfo(mainViewModel.uuid)
            }
        }
    }

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

