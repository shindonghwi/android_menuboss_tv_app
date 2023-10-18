package com.orot.menuboss_tv.ui.screens.auth

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.hilt.navigation.compose.hiltViewModel
import com.orot.menuboss_tv.MainActivity
import com.orot.menuboss_tv.presentation.R
import com.orot.menuboss_tv.ui.components.RiveAnimation
import com.orot.menuboss_tv.ui.compose.modifier.tvSafeArea
import com.orot.menuboss_tv.ui.compose.painter.rememberQrBitmapPainter
import com.orot.menuboss_tv.ui.model.UiState
import com.orot.menuboss_tv.ui.navigations.LocalMainViewModel
import com.orot.menuboss_tv.ui.navigations.LocalNavController
import com.orot.menuboss_tv.ui.navigations.RouteScreen
import com.orot.menuboss_tv.ui.source_pack.IconPack
import com.orot.menuboss_tv.ui.source_pack.iconpack.Logo
import com.orot.menuboss_tv.ui.theme.AdjustedBoldText
import com.orot.menuboss_tv.ui.theme.AdjustedMediumText
import com.orot.menuboss_tv.ui.theme.colorBackground
import com.orot.menuboss_tv.ui.theme.colorWhite
import com.orot.menuboss_tv.utils.adjustedDp
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("HardwareIds")
@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val activity = context as MainActivity
    val navController = LocalNavController.current
    val mainViewModel = LocalMainViewModel.current

    val doMenuScreenActionState = authViewModel.navigateToMenuState.collectAsState().value
    val accessTokenState = authViewModel.accessToken.collectAsState().value

    BackHandler { activity.finish() }

    /**
     * @feature: 디바이스 정보 요청 & ConnectStream 구독
     * @author: 2023/10/15 1:11 PM donghwishin
     * @description{
     *   QR Url, QR Code 를 가져오기 위해서는 디바이스 정보가 필요합니다.
     * }
     */
    LaunchedEffect(key1 = Unit, block = {
        val uuid = mainViewModel.getUUID()
        authViewModel.requestGetDeviceInfo(uuid = uuid)
        mainViewModel.subscribeConnectStream()
    })

    /**
     * @feature: accessToken의 상태가 갱신되면, connectStream 을 해제 후, 재 구독합니다.
     * @author: 2023/10/15 1:35 PM donghwishin
     */
    LaunchedEffect(key1 = accessTokenState, block = {
        accessTokenState?.let { token ->
            mainViewModel.updateAccessToken(token)
            authViewModel.triggerMenuState(true)
        }
    })

    /**
     * @feature: ConnectEventResponse.ConnectEvent.ENTRY 상태가 되면,
     * MenuBoardScreen 으로 이동합니다.
     *
     * @author: 2023/10/02 6:19 PM donghwishin
     *
     * @description{
     *   ConnectEvent.ENTRY: 스크린 등록 이벤트
     * }
     */
    val grpcCode = mainViewModel.grpcStatusCode.collectAsState().value
    LaunchedEffect(key1 = grpcCode, block = {
        CoroutineScope(Dispatchers.Main).launch {
            when (grpcCode) {
                ConnectEventResponse.ConnectEvent.ENTRY.number -> {
                    val uuid = mainViewModel.getUUID()
                    authViewModel.requestGetDeviceInfo(uuid)
                }
            }
        }
    })

    /**
     * @feature: 메뉴판 화면으로 이동하는 기능
     * @author: 2023/10/12 1:06 PM donghwishin
     */
    DisposableEffect(key1 = doMenuScreenActionState, effect = {
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
    })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorBackground)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .tvSafeArea()
                .fillMaxSize(),
            constraintSet = createConstraintSet(context = context)
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
            text = "Welcome to the MenuBoss Smart TV APP", fontSize = adjustedDp(32.dp)
        )
        AdjustedMediumText(
            modifier = Modifier.padding(top = adjustedDp(8.dp)),
            text = "To run the Menu Boss Tv app  Register your TV screen in two ways",
            fontSize = adjustedDp(16.dp)
        )
    }
}

@Composable
private fun BodyContent(
    modifier: Modifier,
    authViewModel: AuthViewModel
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
//                OrDivider()
//                QRCode(modifier = Modifier.weight(1f), qrUrl = qrUrl)
            }
        }

        else -> Loading()
    }
}

@Composable
private fun FooterContent(modifier: Modifier) {
    AdjustedMediumText(
        modifier = modifier,
        text = "Do not turn off the TV screen until you connect it. \n" + "If you have any questions, please contact the website",
        fontSize = adjustedDp(14.dp),
        color = colorWhite.copy(alpha = 0.8f),
    )
}

@Composable
private fun PinCode(modifier: Modifier, code: String?) {

    val alpha: Float by animateFloatAsState(
        targetValue = if (code != null) 1f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = ""
    )

    Column(
        modifier = modifier.alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        AdjustedBoldText(text = "Enter Pin Code", fontSize = adjustedDp(24.dp))

        code?.let {
            Row(
                modifier = Modifier
                    .padding(top = adjustedDp(40.dp)) // 70.dp
                    .height(adjustedDp(60.dp)), // 48.dp
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                it.forEachIndexed { index, number ->
                    run {
                        AdjustedBoldText(
                            modifier = Modifier.size(adjustedDp(60.dp)), // 48.dp
                            text = number.toString(),
                            fontSize = adjustedDp(48.dp) // 24.dp
                        )

                        if (index != code.lastIndex) Box(
                            Modifier.fillMaxHeight(), contentAlignment = Alignment.Center
                        ) {
                            Spacer(
                                Modifier
                                    .width(adjustedDp(12.dp))
                                    .height(adjustedDp(2.5.dp))
                                    .background(colorWhite)
                            )
                        }
                    }
                }
            }
        }

        AdjustedMediumText(
            modifier = Modifier.padding(top = adjustedDp(20.dp)),
            text = "Visit MenuBoss website or mobile app\nAnd enter the code below",
            fontSize = adjustedDp(16.dp),
            color = colorWhite.copy(alpha = 0.5f),
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

        AdjustedBoldText(
            modifier = Modifier.padding(vertical = adjustedDp(12.dp)),
            text = "OR",
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
        targetValue = if (qrUrl != null) 1f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = ""
    )

    Column(
        modifier = modifier.alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        AdjustedBoldText(
            text = "Scan QR Code",
            fontSize = adjustedDp(24.dp),
        )

        qrUrl?.let {
            Image(
                modifier = Modifier
                    .padding(top = adjustedDp(40.dp))
                    .size(adjustedDp(180.dp)),
                painter = rememberQrBitmapPainter(it),
                contentDescription = "QR Code",
                contentScale = ContentScale.FillBounds,
            )
        }
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
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