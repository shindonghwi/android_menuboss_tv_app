package com.orot.menuboss_tv.ui.navigations

sealed class RouteScreen(val route: String) {

    // 스플래시 화면
    data object SplashScreen : RouteScreen("/splash")

    // 인증 화면
    data object AuthScreen : RouteScreen("/auth")


    // 데이터 업데이트 화면
    data object ReloadScreen : RouteScreen("/reload")

    // 메뉴보드 화면
    data object MenuBoardScreen : RouteScreen("/menu_board")

}
