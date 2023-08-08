package com.orot.menuboss_tv.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout

fun Modifier.tvSafeArea(): Modifier {
    return this.layout { measurable, constraints ->
        // Calculate 5% padding for both horizontal and vertical
        val horizontalPadding = (constraints.maxWidth * 0.05).toInt()
        val verticalPadding = (constraints.maxHeight * 0.05).toInt()
        val placeable = measurable.measure(constraints.copy(
            maxWidth = constraints.maxWidth - 2 * horizontalPadding,
            maxHeight = constraints.maxHeight - 2 * verticalPadding
        ))

        layout(placeable.width, placeable.height) {
            placeable.place(horizontalPadding, verticalPadding)
        }
    }
}