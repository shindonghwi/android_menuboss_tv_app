package com.orot.menuboss_tv_kr.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun adjustedDp(original: Dp): Dp {
    val densityDpi = LocalConfiguration.current.densityDpi
    val adjustmentFactor = when {
        densityDpi <= 120 -> 0.75f // For ldpi
        densityDpi <= 160 -> 1.0f // For mdpi
        densityDpi <= 240 -> 1.25f // For hdpi
        densityDpi <= 480 -> 1.5f // Adjusted for better visibility in xhdpi & xxhdpi
        else -> 2.0f // For xxxhdpi and above
    }

    val adjustedValue = original.value / adjustmentFactor
    return adjustedValue.dp
}

fun adjustedDp(original: Dp, densityDpi: Int): Dp {
    val adjustmentFactor = when {
        densityDpi <= 120 -> 0.75f // For ldpi
        densityDpi <= 160 -> 1.0f // For mdpi
        densityDpi <= 240 -> 1.25f // For hdpi
        densityDpi <= 480 -> 1.5f // Adjusted for better visibility in xhdpi & xxhdpi
        else -> 2.0f // For xxxhdpi and above
    }

    val adjustedValue = original.value / adjustmentFactor
    return adjustedValue.dp
}
