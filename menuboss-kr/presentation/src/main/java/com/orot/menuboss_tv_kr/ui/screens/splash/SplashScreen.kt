package com.orot.menuboss_tv_kr.ui.screens.splash

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import app.rive.runtime.kotlin.core.Loop
import com.orot.menuboss_tv_kr.domain.constants.MENUBOSS_AMAZON_STORE_URL
import com.orot.menuboss_tv_kr.domain.constants.MENUBOSS_GOOGLE_STORE_URL
import com.orot.menuboss_tv_kr.presentation.R
import com.orot.menuboss_tv_kr.ui.components.RiveAnimation
import com.orot.menuboss_tv_kr.ui.navigations.LocalMenuBoardViewModel
import com.orot.menuboss_tv_kr.ui.navigations.LocalNavController
import com.orot.menuboss_tv_kr.ui.navigations.RouteScreen
import com.orot.menuboss_tv_kr.ui.theme.AdjustedBoldText
import com.orot.menuboss_tv_kr.ui.theme.AdjustedMediumText
import com.orot.menuboss_tv_kr.ui.theme.AdjustedRegularText
import com.orot.menuboss_tv_kr.ui.theme.AdjustedSemiBoldText
import com.orot.menuboss_tv_kr.ui.theme.colorBackground
import com.orot.menuboss_tv_kr.ui.theme.colorGray300
import com.orot.menuboss_tv_kr.ui.theme.colorGray700
import com.orot.menuboss_tv_kr.ui.theme.colorGray900
import com.orot.menuboss_tv_kr.utils.adjustedDp
import focusableWithClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * @description{
 *   TODO: 스플래시 화면에서 에러 처리나 로딩중일때 화면에 표시 해야 할 것들이 있음.
 * }
 */

@Composable
fun SplashScreen(
    splashViewModel: SplashViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val context = LocalContext.current

    val menuBoardViewModel = LocalMenuBoardViewModel.current
    val doAuthScreenActionState = splashViewModel.navigateToAuthState.collectAsState().value
    val doMenuScreenActionState = splashViewModel.navigateToMenuState.collectAsState().value
    val forceUpdateState = splashViewModel.forceUpdateState.collectAsState().value
    val resumedOnce = remember { mutableStateOf(false) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
            } else if (event == Lifecycle.Event.ON_PAUSE) {
            } else if (event == Lifecycle.Event.ON_RESUME && resumedOnce.value) {
                CoroutineScope(Dispatchers.Main).launch {
//                    splashViewModel.requestGetDeviceInfo(uuid = uuid, appVersion = getAppVersion(context))
                }
            } else if (event == Lifecycle.Event.ON_RESUME) {
                // 첫 번째 onResume 호출에 대해 감지하고 상태를 업데이트합니다.
                resumedOnce.value = true
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    /**
     * @feature: 디바이스 정보 요청
     * @author: 2023/10/15 12:52 PM donghwishin
     */
    LaunchedEffect(key1 = Unit, block = {
        getAppVersion(context).let { appVersion ->
            splashViewModel.run {
                requestUpdateUUID(context = context, appVersion = appVersion)
                delay(1000) // riv animation 최초로 끝나는 시간을 기다립니다.
                requestGetDeviceInfo(appVersion = appVersion)
            }
        }
    })

    /**
     * @feature: 디바이스 정보 요청
     * @author: 2023/10/15 12:52 PM donghwishin
     */
    LaunchedEffect(key1 = forceUpdateState, block = {
        if (forceUpdateState == FORCE_UPDATE_STATE.ERROR) {
            Toast.makeText(context, "Failed to get update information.", Toast.LENGTH_SHORT).show()
        }
    })

    /**
     * @feature: 인증화면으로 이동하는 기능
     * @author: 2023/10/12 1:06 PM donghwishin
     */
    DisposableEffect(key1 = doAuthScreenActionState, effect = {
        if (doAuthScreenActionState) {
            menuBoardViewModel.updateUUID(splashViewModel.getCurrentUUID())
            navController.navigate(RouteScreen.AuthScreen.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
        onDispose {
            splashViewModel.initState()
        }
    })

    /**
     * @feature: 메뉴판 화면으로 이동하는 기능
     * @author: 2023/10/12 1:06 PM donghwishin
     */
    DisposableEffect(key1 = doMenuScreenActionState, effect = {
        if (doMenuScreenActionState) {
            menuBoardViewModel.updateUUID(splashViewModel.getCurrentUUID())
            navController.navigate(RouteScreen.MenuBoardScreen.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
        onDispose {
            splashViewModel.initState()
        }
    })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorBackground), contentAlignment = Alignment.Center
    ) {
        RiveAnimation(
            animation = R.raw.logo,
            onInit = {
                it.play(loop = Loop.ONESHOT)
            },
            onAnimEnd = {},
        )

        if (forceUpdateState == FORCE_UPDATE_STATE.FORCE_UPDATE) {
            ForceUpdateUI()
        } else if (forceUpdateState == FORCE_UPDATE_STATE.ERROR) {

        }

    }
}

@Composable
private fun ForceUpdateUI() {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .width(adjustedDp(640.dp))
            .background(colorGray900),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AdjustedSemiBoldText(
            modifier = Modifier
                .padding(horizontal = adjustedDp(212.dp))
                .padding(
                    top = adjustedDp(24.dp), bottom = adjustedDp(16.dp)
                ),
            text = stringResource(id = R.string.splash_update_title), fontSize = adjustedDp(20.dp)
        )

        Divider(color = colorGray700)

        AdjustedRegularText(
            modifier = Modifier
                .padding(horizontal = adjustedDp(84.dp))
                .padding(
                    top = adjustedDp(24.dp),
                ),
            text = stringResource(id = R.string.splash_update_description1),
            fontSize = adjustedDp(16.dp)
        )

        AdjustedBoldText(
            modifier = Modifier
                .padding(horizontal = adjustedDp(76.dp))
                .padding(
                    top = adjustedDp(4.dp), bottom = adjustedDp(24.dp)
                ),
            text = stringResource(id = R.string.splash_update_description2),
            fontSize = adjustedDp(16.dp)
        )

        Box(
            modifier = Modifier
                .padding(horizontal = adjustedDp(160.dp))
                .padding(
                    top = adjustedDp(12.dp), bottom = adjustedDp(24.dp)
                )
                .background(colorGray300)
                .focusableWithClick {
                    openAmazonAppStore(context)
                }, contentAlignment = Alignment.Center
        ) {
            AdjustedMediumText(
                modifier = Modifier.padding(
                    horizontal = adjustedDp(120.dp),
                    vertical = adjustedDp(14.dp)
                ),
                text = stringResource(id = R.string.splash_update_button),
                fontSize = adjustedDp(14.dp),
                color = colorGray900
            )
        }
    }
}

/**
 * @feature: 앱 버전 반환
 * @author: 2023/11/16 3:00 PM donghwishin
 */
private fun getAppVersion(context: Context): String {
    return context.packageManager.getPackageInfo(context.packageName, 0).versionName
}

/**
 * @feature: MenuBoss TV Amazon App Store를 엽니다.
 * @author: 2023/11/16 2:43 PM donghwishin
 */
private fun openAmazonAppStore(context: Context) {
    val isFirsOs = Build.MANUFACTURER.lowercase(Locale.getDefault()).contains("amazon")
    val storeUrl = if (isFirsOs) {
        MENUBOSS_AMAZON_STORE_URL
    } else {
        MENUBOSS_GOOGLE_STORE_URL
    }

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(storeUrl)
        setPackage("com.android.vending") // Google Play Store package
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(storeUrl)
                }
            )
        } catch (e: ActivityNotFoundException) {
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl))
            context.startActivity(fallbackIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Error: $e", Toast.LENGTH_SHORT).show()
        }
    }
}