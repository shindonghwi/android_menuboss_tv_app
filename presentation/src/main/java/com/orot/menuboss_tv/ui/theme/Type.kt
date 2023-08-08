package com.orot.menuboss_tv.ui.theme


import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.orot.menuboss_tv.presentation.R

val Manrope = FontFamily(
    Font(R.font.manrope_extralight, FontWeight.W200),
    Font(R.font.manrope_light, FontWeight.W300),
    Font(R.font.manrope_regular, FontWeight.W400),
    Font(R.font.manrope_medium, FontWeight.W500),
    Font(R.font.manrope_semibold, FontWeight.W600),
    Font(R.font.manrope_bold, FontWeight.W700),
    Font(R.font.manrope_extrabold, FontWeight.W800),
)

class ManropeTypo {

    companion object {
        private val baseStyle = TextStyle(
            fontFamily = Manrope,
            textAlign = TextAlign.Center,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            )
        )

        @Composable
        fun getAdjustSize(fontSize: Float): Float {
            val resources = LocalContext.current.resources
            val density = resources.displayMetrics.density
            return fontSize / density
        }

        val bold = baseStyle.copy(
            fontWeight = FontWeight.Bold,
        )

        val medium = baseStyle.copy(
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
fun AdjustedBoldText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: Dp,
    color: Color = colorWhite
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = ManropeTypo.bold.copy(color = color, fontSize = fontSize.value.sp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun AdjustedMediumText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: Dp,
    color: Color = colorWhite
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = ManropeTypo.medium.copy(color = color, fontSize = fontSize.value.sp),
            textAlign = TextAlign.Center,
        )
    }
}