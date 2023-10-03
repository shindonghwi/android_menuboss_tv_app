package com.orot.menuboss_tv.ui.navigations

//import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.orot.menuboss_tv.MainViewModel
import com.orot.menuboss_tv.ui.screens.auth.AuthScreen
import com.orot.menuboss_tv.ui.screens.menu_board.MenuBoardScreen
import com.orot.menuboss_tv.ui.screens.splash.SplashScreen

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavHostController provided")
}

@Composable
fun Navigation(
    mainViewModel: MainViewModel = hiltViewModel<MainViewModel>()
) {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(navController = navController, startDestination = RouteScreen.SplashScreen.route) {
            composable(RouteScreen.SplashScreen.route) {
                SplashScreen(mainViewModel = mainViewModel)
            }
            composable("${RouteScreen.AuthScreen.route}/{code}/{qrUrl}") {
                val code = it.arguments?.getString("code")
                val qrUrl = it.arguments?.getString("qrUrl")
                AuthScreen(mainViewModel = mainViewModel, code = code, qrUrl = qrUrl)
            }
            composable(RouteScreen.MenuBoardScreen.route) {
                MenuBoardScreen()
            }
        }
    }
}

