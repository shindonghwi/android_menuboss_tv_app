package com.orot.menuboss_tv.ui.navigations

sealed class RouteScreen(val route: String) {

    // 스플래시 화면
    object SplashScreen : RouteScreen("/splash")

    // 인증 화면
    object AuthScreen : RouteScreen("/auth")


    // 데이터 업데이트 화면
    object ReloadScreen : RouteScreen("/reload")

}
