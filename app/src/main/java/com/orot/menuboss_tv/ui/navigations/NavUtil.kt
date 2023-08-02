package com.orot.menuboss_tv.ui.navigations

import androidx.navigation.NavController
import androidx.navigation.NavHostController

/** @feature: 목적지로 화면을 이동하는 기능
 * @author: 2023/01/09 3:25 PM donghwishin
 */
fun NavHostController.navigateTo(route: String, isLaunchSingleTop: Boolean = true) {
    navigate(route) {
        launchSingleTop = isLaunchSingleTop
    }
}

/** @feature: 목적지로 화면을 이동하고 popUpRoute 를 선언한 위치이전 스택을 지운다.
 * @author: 2023/01/09 3:26 PM donghwishin
 * @description{
 *  A -> B -> C -> D
 *
 *  ex) C화면에서 D화면으로 이동하면서 popUpRoute를 A로 지정한 경우
 *  A -> B -> C(popUpRoute) -> D(Back Pressed Event) -> A 로 이동된다.
 */
fun NavHostController.navigateWithPopUp(route: String, popUpRoute: RouteScreen) {
    navigate(route) {
        findExistRouteName(popUpRoute)?.let {
            popUpTo(it) {
                inclusive = true
            }
        }
    }
}

private fun NavController.findExistRouteName(screen: RouteScreen): String? {
    return backQueue.firstOrNull {
        it.destination.route?.contains(screen.route) == true
    }?.destination?.route
}
