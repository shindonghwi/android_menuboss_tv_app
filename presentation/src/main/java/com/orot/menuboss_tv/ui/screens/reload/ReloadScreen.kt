package com.orot.menuboss_tv.ui.screens.reload

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.orot.menuboss_tv.presentation.R
import com.orot.menuboss_tv.ui.components.RiveAnimation
import com.orot.menuboss_tv.ui.compose.modifier.tvSafeArea
import com.orot.menuboss_tv.ui.source_pack.IconPack
import com.orot.menuboss_tv.ui.source_pack.iconpack.Logo
import com.orot.menuboss_tv.ui.theme.AdjustedBoldText
import com.orot.menuboss_tv.ui.theme.AdjustedMediumText
import com.orot.menuboss_tv.ui.theme.colorBackground
import com.orot.menuboss_tv.ui.theme.colorWhite
import com.orot.menuboss_tv.utils.adjustedDp


@Composable
fun ReloadScreen() {
    val context = LocalContext.current

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
        top.linkTo(parent.top)
        bottom.linkTo(parent.bottom)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
    }
    constrain(footer) {
        bottom.linkTo(parent.bottom, margin = adjustedDp(16.dp, dpi))
        end.linkTo(parent.end, margin = adjustedDp(16.dp, dpi))
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
            text = "Changing MenuBoss TV App settings", fontSize = adjustedDp(32.dp)
        )
        AdjustedMediumText(
            modifier = Modifier.padding(top = adjustedDp(8.dp)),
            text = "Please don't turn off the screen until the setting is done",
            fontSize = adjustedDp(16.dp)
        )
    }
}

@Composable
private fun BodyContent(modifier: Modifier) {
    RiveAnimation(
        modifier = modifier,
        animation = R.raw.loading,
        onInit = {
            it.play()
        },
        onAnimEnd = {},
    )
}


@Composable
private fun FooterContent(modifier: Modifier) {
    AdjustedMediumText(
        modifier = modifier,
        text = "TV Name : 109230948",
        fontSize = adjustedDp(14.dp),
        color = colorWhite.copy(alpha = 0.8f)
    )
}