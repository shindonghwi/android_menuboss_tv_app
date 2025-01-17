package com.orot.menuboss_tv_kr.ui.screens.auth

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.orot.menuboss_tv_kr.domain.constants.WEB_LOGIN_URL
import com.orot.menuboss_tv_kr.presentation.R
import com.orot.menuboss_tv_kr.ui.components.RiveAnimation
import com.orot.menuboss_tv_kr.ui.compose.modifier.tvSafeArea
import com.orot.menuboss_tv_kr.ui.compose.painter.rememberQrBitmapPainter
import com.orot.menuboss_tv_kr.ui.model.UiState
import com.orot.menuboss_tv_kr.ui.navigations.LocalMenuBoardViewModel
import com.orot.menuboss_tv_kr.ui.navigations.LocalNavController
import com.orot.menuboss_tv_kr.ui.navigations.RouteScreen
import com.orot.menuboss_tv_kr.ui.source_pack.IconPack
import com.orot.menuboss_tv_kr.ui.source_pack.iconpack.Logo
import com.orot.menuboss_tv_kr.ui.theme.AdjustedBoldText
import com.orot.menuboss_tv_kr.ui.theme.AdjustedMediumText
import com.orot.menuboss_tv_kr.ui.theme.AdjustedRegularText
import com.orot.menuboss_tv_kr.ui.theme.AdjustedSemiBoldText
import com.orot.menuboss_tv_kr.ui.theme.colorBackground
import com.orot.menuboss_tv_kr.ui.theme.colorGray100
import com.orot.menuboss_tv_kr.ui.theme.colorGray400
import com.orot.menuboss_tv_kr.ui.theme.colorLightSkyBlue
import com.orot.menuboss_tv_kr.ui.theme.colorWhite
import com.orot.menuboss_tv_kr.utils.adjustedDp
import focusableWithClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("HardwareIds")
@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val navController = LocalNavController.current

    val menuBoardViewModel = LocalMenuBoardViewModel.current

    val doMenuScreenActionState = authViewModel.navigateToMenuState.collectAsState().value

    val resumedOnce = remember { mutableStateOf(false) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
            } else if (event == Lifecycle.Event.ON_RESUME && resumedOnce.value) {
                CoroutineScope(Dispatchers.Main).launch {
                    authViewModel.requestGetDeviceInfo(uuid = menuBoardViewModel.getUUID())
                }
            } else if (event == Lifecycle.Event.ON_RESUME) {
                // 첫 번째 onResume 호출에 대해 감지하고 상태를 업데이트합니다.
                resumedOnce.value = true
            } else if (event == Lifecycle.Event.ON_PAUSE) {
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    /**
     * @feature: 디바이스 정보 요청 & ConnectStream 구독
     * @author: 2023/10/15 1:11 PM donghwishin
     * @description{
     *   QR Url, QR Code 를 가져오기 위해서는 디바이스 정보가 필요합니다.
     * }
     */
    LaunchedEffect(key1 = Unit, block = {
        authViewModel.startProcess(menuBoardViewModel.getUUID())
    })

    /**
     * @feature: 메뉴판 화면으로 이동하는 기능
     * @author: 2023/10/12 1:06 PM donghwishin
     */
    DisposableEffect(key1 = doMenuScreenActionState) {
        if (doMenuScreenActionState) {
            navController.navigate(RouteScreen.MenuBoardScreen.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
        onDispose {
            authViewModel.triggerMenuState(false)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorBackground)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .tvSafeArea()
                .fillMaxSize(), constraintSet = createConstraintSet(context = context)
        ) {
            LogoImage(modifier = Modifier.layoutId("logo"))

            HeaderContent(modifier = Modifier.layoutId("header"))

            BodyContent(modifier = Modifier.layoutId("body"), authViewModel = authViewModel)

            FooterContent(modifier = Modifier.layoutId("footer"))
        }
    }

}

private fun createConstraintSet(context: Context) = ConstraintSet {

    val dpi = context.resources.displayMetrics.densityDpi

    val logo = createRefFor("logo")
    val header = createRefFor("header")
    val body = createRefFor("body")
    val footer = createRefFor("footer")

    constrain(logo) {
        top.linkTo(parent.top, margin = adjustedDp(16.dp, dpi))
        end.linkTo(parent.end, margin = adjustedDp(16.dp, dpi))
    }

    constrain(header) {
        top.linkTo(logo.bottom, margin = adjustedDp(5.dp, dpi))
        start.linkTo(parent.start)
        end.linkTo(parent.end)
    }

    constrain(body) {
        top.linkTo(header.bottom)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        bottom.linkTo(footer.top)
    }

    constrain(footer) {
        bottom.linkTo(parent.bottom, margin = adjustedDp(65.dp, dpi))
        start.linkTo(parent.start)
        end.linkTo(parent.end)
    }
}

@Composable
private fun LogoImage(modifier: Modifier) {
    Image(
        modifier = modifier.size(
            width = adjustedDp(88.dp),
            height = adjustedDp(44.dp),
        ), imageVector = IconPack.Logo, contentDescription = ""
    )
}


@Composable
private fun HeaderContent(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AdjustedBoldText(
            text = stringResource(id = R.string.auth_title), fontSize = adjustedDp(32.dp)
        )
        AdjustedRegularText(
            modifier = Modifier.padding(top = adjustedDp(8.dp)),
            text = stringResource(id = R.string.auth_subtitle),
            fontSize = adjustedDp(16.dp),
            color = colorGray100
        )
    }
}

@Composable
private fun BodyContent(
    modifier: Modifier, authViewModel: AuthViewModel
) {

    when (val codeInfoState = authViewModel.pairCodeInfo.collectAsState().value) {
        is UiState.Success -> {
            val code = codeInfoState.data?.first
            val qrUrl = codeInfoState.data?.second
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PinCode(modifier = Modifier.weight(1f), code = code)
                OrDivider()
                QRCode(modifier = Modifier.weight(1f), qrUrl = qrUrl)
            }
        }

        else -> Loading()
    }
}

@Composable
private fun PinCode(
    modifier: Modifier, code: String?,
) {

    val context = LocalContext.current

    val alpha: Float by animateFloatAsState(
        targetValue = if (code != null) 1f else 0f, animationSpec = tween(durationMillis = 700), label = ""
    )

    Column(
        modifier = modifier.alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        AdjustedSemiBoldText(
            text = stringResource(id = R.string.auth_enter_pin_code),
            fontSize = adjustedDp(24.dp)
        )

        AdjustedBoldText(
            modifier = Modifier
                .padding(top = adjustedDp(20.dp))
                .fillMaxWidth(), // 48.dp
            text = code.toString(), letterSpacing = 0.5, fontSize = adjustedDp(100.dp) // 24.dp
        )

        AdjustedRegularText(
            modifier = Modifier.padding(top = adjustedDp(20.dp)),
            text = stringResource(id = R.string.auth_enter_pin_code_description),
            fontSize = adjustedDp(14.dp),
            color = colorGray400
        )

        AdjustedMediumText(
            modifier = Modifier
                .padding(top = adjustedDp(4.dp))
                .focusableWithClick {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(WEB_LOGIN_URL)
                        }
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(WEB_LOGIN_URL))
                        context.startActivity(fallbackIntent)
                    } catch (e: Exception) {
                        Toast
                            .makeText(context, context.getString(R.string.message_error), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .padding(vertical = adjustedDp(8.dp), horizontal = adjustedDp(12.dp)),
            text = WEB_LOGIN_URL,
            fontSize = adjustedDp(16.dp),
            color = colorLightSkyBlue,
        )
    }
}

@Composable
private fun OrDivider() {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .width(adjustedDp(1.dp))
                .height(adjustedDp(120.dp))
                .background(colorWhite)
        )

        AdjustedSemiBoldText(
            modifier = Modifier.padding(vertical = adjustedDp(12.dp)),
            text = stringResource(id = R.string.common_or),
            fontSize = adjustedDp(16.dp),
        )

        Box(
            modifier = Modifier
                .width(adjustedDp(1.dp))
                .height(adjustedDp(120.dp))
                .background(colorWhite)
        )
    }
}

@Composable
private fun QRCode(modifier: Modifier, qrUrl: String?) {

    val alpha: Float by animateFloatAsState(
        targetValue = if (qrUrl != null) 1f else 0f, animationSpec = tween(durationMillis = 700), label = ""
    )

    Column(
        modifier = modifier.alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        AdjustedSemiBoldText(
            text = stringResource(id = R.string.auth_scan_qr_code),
            fontSize = adjustedDp(24.dp),
        )

        qrUrl?.let {
            Image(
                modifier = Modifier
                    .padding(top = adjustedDp(40.dp))
                    .size(adjustedDp(184.dp)),
                painter = rememberQrBitmapPainter(it),
                contentDescription = "QR Code",
                contentScale = ContentScale.FillBounds,
            )
        }
    }
}

@Composable
private fun FooterContent(modifier: Modifier) {
    AdjustedMediumText(
        modifier = modifier,
        text = "${stringResource(id = R.string.auth_description1)}\n${stringResource(id = R.string.auth_description2)}",
        fontSize = adjustedDp(14.dp),
        color = colorGray100
    )
}


@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        RiveAnimation(
            animation = R.raw.loading,
            onInit = {
                it.play()
            },
            onAnimEnd = {},
        )
    }
}