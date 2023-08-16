package com.orot.menuboss_tv.ui.navigations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.orot.menuboss_tv.ui.screens.auth.AuthScreen
import com.orot.menuboss_tv.ui.screens.auth.AuthScreenViewModel
import com.orot.menuboss_tv.ui.screens.menu_board.MenuBoardScreen
import com.orot.menuboss_tv.ui.screens.menu_board.MenuBoardScreenViewModel
import com.orot.menuboss_tv.ui.screens.reload.ReloadScreen
import com.orot.menuboss_tv.ui.screens.splash.SplashScreen

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavHostController provided")
}

@Composable
fun Navigation() {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(navController = navController, startDestination = RouteScreen.SplashScreen.route) {
            composable(RouteScreen.SplashScreen.route) {
                SplashScreen()
            }
            composable(RouteScreen.AuthScreen.route) {
                AuthScreen()
            }
            composable(RouteScreen.ReloadScreen.route) {
                ReloadScreen()
            }
            composable(RouteScreen.MenuBoardScreen.route) {
                MenuBoardScreen()
            }
        }
    }
}

