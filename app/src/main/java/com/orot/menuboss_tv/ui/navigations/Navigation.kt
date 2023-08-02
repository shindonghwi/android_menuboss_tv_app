package com.orot.menuboss_tv.ui.navigations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.orot.menuboss_tv.ui.screens.DetailsError
import com.orot.menuboss_tv.ui.screens.auth.AuthScreen
import com.orot.menuboss_tv.ui.screens.splash.SplashScreen

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavHostController provided")
}

@Composable
fun Navigation() {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(navController = navController, startDestination = "/splash") {
            composable("/splash") {
                SplashScreen()
            }
            composable("/auth") {
                AuthScreen()
            }

            composable(
                route = "/movie/{id}",
                arguments = listOf(navArgument("id") {
                    type = NavType.LongType
                })
            ) {
                if (it.arguments?.getLong("id") == null) {
                    throw DetailsError.NoIdSpecified
                }
//                DetailsScreen()
            }
        }
    }
}