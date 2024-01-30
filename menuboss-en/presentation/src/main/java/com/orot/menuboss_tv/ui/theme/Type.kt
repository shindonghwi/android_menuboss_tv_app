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
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.orot.menuboss_tv.presentation.R

val Pretendard = FontFamily(
    Font(R.font.pretendard_thin, FontWeight.W100),
    Font(R.font.pretendard_extralight, FontWeight.W200),
    Font(R.font.pretendard_light, FontWeight.W300),
    Font(R.font.pretendard_regular, FontWeight.W400),
    Font(R.font.pretendard_medium, FontWeight.W500),
    Font(R.font.pretendard_semibold, FontWeight.W600),
    Font(R.font.pretendard_bold, FontWeight.W700),
    Font(R.font.pretendard_extrabold, FontWeight.W800),
    Font(R.font.pretendard_black, FontWeight.W900),
)

class PretendardTypo {

    companion object {
        private val baseStyle = TextStyle(
            fontFamily = Pretendard,
            textAlign = TextAlign.Center,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            )
        )

        val bold = baseStyle.copy(
            fontWeight = FontWeight.Bold,
        )

        val medium = baseStyle.copy(
            fontWeight = FontWeight.Medium,
        )

        val semiBold = baseStyle.copy(
            fontWeight = FontWeight.SemiBold,
        )

        val regular = baseStyle.copy(
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
fun AdjustedBoldText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: Dp,
    letterSpacing: Double = 0.0,
    color: Color = colorWhite
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = PretendardTypo.bold.copy(
                color = color,
                fontSize = fontSize.value.sp,
                letterSpacing = letterSpacing.em,
            ),
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
            style = PretendardTypo.medium.copy(
                color = color,
                fontSize = fontSize.value.sp
            ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun AdjustedRegularText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: Dp,
    color: Color = colorWhite
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = PretendardTypo.regular.copy(
                color = color,
                fontSize = fontSize.value.sp
            ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun AdjustedSemiBoldText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: Dp,
    color: Color = colorWhite
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = PretendardTypo.semiBold.copy(
                color = color,
                fontSize = fontSize.value.sp
            ),
            textAlign = TextAlign.Center,
        )
    }
}