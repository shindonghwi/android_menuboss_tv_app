package com.orot.menuboss_tv.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun adjustedDp(original: Dp): Dp {
    val densityDpi = LocalConfiguration.current.densityDpi

    val adjustmentFactor = when {
        densityDpi <= 160 -> 1.0f   // For mdpi and lower
        densityDpi <= 320 -> 1.5f   // For xhdpi
        densityDpi <= 480 -> 2.0f   // For xxhdpi
        else -> 2.5f                // For xxxhdpi and above
    }

    val adjustedValue = original.value / adjustmentFactor
    return adjustedValue.dp
}

fun adjustedDp(original: Dp, densityDpi: Int): Dp {
    val adjustmentFactor = when {
        densityDpi <= 160 -> 1.0f   // For mdpi and lower
        densityDpi <= 320 -> 1.5f   // For xhdpi
        densityDpi <= 480 -> 2.0f   // For xxhdpi
        else -> 2.5f                // For xxxhdpi and above
    }

    val adjustedValue = original.value / adjustmentFactor
    return adjustedValue.dp
}
