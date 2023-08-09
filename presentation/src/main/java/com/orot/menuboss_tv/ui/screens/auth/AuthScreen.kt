package com.orot.menuboss_tv.ui.screens.auth

import android.content.Context
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.orot.menuboss_tv.ui.compose.modifier.tvSafeArea
import com.orot.menuboss_tv.ui.compose.painter.rememberQrBitmapPainter
import com.orot.menuboss_tv.ui.source_pack.IconPack
import com.orot.menuboss_tv.ui.source_pack.iconpack.Logo
import com.orot.menuboss_tv.ui.theme.AdjustedBoldText
import com.orot.menuboss_tv.ui.theme.AdjustedMediumText
import com.orot.menuboss_tv.ui.theme.colorBackground
import com.orot.menuboss_tv.ui.theme.colorWhite
import com.orot.menuboss_tv.utils.adjustedDp


@Composable
fun AuthScreen(authScreenViewModel: AuthScreenViewModel) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit, block = {
        authScreenViewModel.requestGetDeviceInfo("1234")
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

            BodyContent(modifier = Modifier.layoutId("body"))

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
        modifier = modifier
            .size(
                width = adjustedDp(88.dp),
                height = adjustedDp(44.dp),
            ),
        imageVector = IconPack.Logo, contentDescription = ""
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
private fun BodyContent(modifier: Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PinCode(modifier = Modifier.weight(1f))
        OrDivider()
        QRCode(modifier = Modifier.weight(1f))
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
private fun PinCode(modifier: Modifier) {

    val pinCode = "1234"

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        AdjustedBoldText(text = "Enter Pin Code", fontSize = adjustedDp(24.dp))

        Row(
            modifier = Modifier
                .padding(top = adjustedDp(70.dp))
                .height(adjustedDp(48.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            pinCode.forEachIndexed { index, number ->
                run {
                    AdjustedBoldText(
                        modifier = Modifier.size(adjustedDp(48.dp)),
                        text = number.toString(),
                        fontSize = adjustedDp(24.dp)
                    )

                    if (index != pinCode.lastIndex) Box(
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
        AdjustedMediumText(
            modifier = Modifier.padding(top = adjustedDp(20.dp)),
            text = "Visit MenuBoss website\nAnd enter the code below",
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
private fun QRCode(modifier: Modifier) {

    val pinCode = "1234"

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        AdjustedBoldText(
            text = "Scan QR Code",
            fontSize = adjustedDp(24.dp),
        )

        Image(
            modifier = Modifier
                .padding(top = adjustedDp(40.dp))
                .size(adjustedDp(180.dp)),
            painter = rememberQrBitmapPainter("AUTH CODE: $pinCode"),
            contentDescription = "QR Code",
            contentScale = ContentScale.FillBounds,
        )
    }
}