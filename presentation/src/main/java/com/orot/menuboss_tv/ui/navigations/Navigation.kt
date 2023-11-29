package com.orot.menuboss_tv.ui.navigations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.orot.menuboss_tv.ui.screens.auth.AuthScreen
import com.orot.menuboss_tv.ui.screens.event.EventScreen
import com.orot.menuboss_tv.ui.screens.menu_board.MenuBoardScreen
import com.orot.menuboss_tv.ui.screens.menu_board.MenuBoardViewModel
import com.orot.menuboss_tv.ui.screens.splash.SplashScreen

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavHostController provided")
}

val LocalMenuBoardViewModel = compositionLocalOf<MenuBoardViewModel> {
    error("No MenuBoardViewModel provided")
}

@Composable
fun Navigation(uuidValue: String) {
    val navController = rememberNavController()
    val menuBoardViewModel = hiltViewModel<MenuBoardViewModel>()

    Box(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalMenuBoardViewModel provides menuBoardViewModel,
        ) {
            NavHost(
                navController = navController, startDestination = RouteScreen.SplashScreen.route
            ) {
                composable(RouteScreen.SplashScreen.route) {
                    SplashScreen(uuid = uuidValue)
                }
                composable(RouteScreen.AuthScreen.route) {
                    AuthScreen(uuid = uuidValue)
                }
                composable(RouteScreen.MenuBoardScreen.route) {
                    MenuBoardScreen(uuid = uuidValue)
                }
            }
            EventScreen()
        }
    }
}
