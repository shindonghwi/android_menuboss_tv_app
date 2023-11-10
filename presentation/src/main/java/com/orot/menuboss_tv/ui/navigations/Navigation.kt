package com.orot.menuboss_tv.ui.navigations

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.datadog.android.Datadog
import com.orot.menuboss_tv.MainViewModel
import com.orot.menuboss_tv.ui.screens.auth.AuthScreen
import com.orot.menuboss_tv.ui.screens.menu_board.MenuBoardScreen
import com.orot.menuboss_tv.ui.screens.splash.SplashScreen

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavHostController provided")
}

val LocalMainViewModel = compositionLocalOf<MainViewModel> {
    error("No MainViewModel provided")
}

@Composable
fun Navigation(uuidValue: String) {
    val navController = rememberNavController()
    val mainViewModel = hiltViewModel<MainViewModel>().apply {
        Datadog.setUserInfo(id = uuidValue, name = Build.MODEL)
        updateUUID(uuidValue)
    }

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalMainViewModel provides mainViewModel
    ) {
        NavHost(
            navController = navController,
            startDestination = RouteScreen.SplashScreen.route
        ) {
            composable(RouteScreen.SplashScreen.route) {
                SplashScreen(uuid = uuidValue)
            }
            composable(RouteScreen.AuthScreen.route) {
                AuthScreen()
            }
            composable(RouteScreen.MenuBoardScreen.route) {
                MenuBoardScreen()
            }
        }
    }
}



