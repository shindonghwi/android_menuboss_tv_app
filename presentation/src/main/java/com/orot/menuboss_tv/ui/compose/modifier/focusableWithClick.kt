import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.focusableWithClick(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    isFocusedColor: Color = Color(0xFFF2C94C),
    notFocusedColor: Color = Color.Transparent,
    onClick: () -> Unit
): Modifier {
    val focusRequester = rememberUpdatedState(FocusRequester())
    val isFocused = interactionSource.collectIsFocusedAsState().value

    return this
        .focusRequester(focusRequester.value)
        .focusable(interactionSource = interactionSource)
        .onKeyEvent { keyEvent ->
            if (keyEvent.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER) {
                onClick()
                true
            } else {
                false
            }
        }
        .background(Color.Transparent)
        .border(1.dp, if (isFocused) isFocusedColor else notFocusedColor, shape = RoundedCornerShape(8.dp))
        .clickable(interactionSource = interactionSource, indication = null) {
            onClick()
        }
}
