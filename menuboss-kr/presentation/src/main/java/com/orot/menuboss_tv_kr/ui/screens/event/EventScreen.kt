package com.orot.menuboss_tv_kr.ui.screens.event

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.orot.menuboss_tv_kr.presentation.R
import com.orot.menuboss_tv_kr.ui.navigations.LocalMenuBoardViewModel
import com.orot.menuboss_tv_kr.ui.theme.AdjustedBoldText
import com.orot.menuboss_tv_kr.ui.theme.colorBackground
import com.orot.menuboss_tv_kr.ui.theme.colorRed500
import com.orot.menuboss_tv_kr.ui.theme.colorWhite
import com.orot.menuboss_tv_kr.utils.adjustedDp
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EventScreen() {
    // Configuration constants
    val animationDelay = 1000
    val animationDuration = 5000

    // Accessing ViewModel and state
    val menuBoardViewModel = LocalMenuBoardViewModel.current
    val eventFlow = menuBoardViewModel.eventCode
    val event = eventFlow.collectAsState(false).value
    val screenName = menuBoardViewModel.screenName

    // State for event handling
    var eventKey by remember { mutableIntStateOf(0) }
    var visible by remember { mutableStateOf(false) }

    // Collecting latest event and updating event key
    LaunchedEffect(Unit) {
        eventFlow.collectLatest { currentEvent ->
            if (currentEvent == ContentEventResponse.ContentEvent.SHOW_SCREEN_NAME) {
                eventKey++
            }
        }
    }

    // Initial visibility handling
    LaunchedEffect(key1 = Unit) {
        visible = true
        delay(animationDelay.toLong())
        visible = false
    }

    // Event-driven visibility handling
    LaunchedEffect(key1 = eventKey) {
        visible = true
        delay(animationDuration.toLong())
        visible = false
    }

    // Display logic based on event type
    event?.let { currentEvent ->
        if (currentEvent == ContentEventResponse.ContentEvent.SHOW_SCREEN_NAME) {
            DisplayScreenName(visible, screenName, animationDelay)
        }
    }
}

@Composable
private fun DisplayScreenName(visible: Boolean, screenName: String, animationDelay: Int) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = animationDelay)),
        exit = fadeOut(animationSpec = tween(durationMillis = animationDelay))
    ) {
        ScreenNameBox(screenName)
    }
}

@Composable
private fun ScreenNameBox(screenName: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(16.dp, colorRed500, RectangleShape)
            .background(colorBackground.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_tv),
                contentDescription = "Localized description",
                modifier = Modifier.size(adjustedDp(80.dp)),
                tint = colorWhite,
            )
            AdjustedBoldText(
                modifier = Modifier.padding(adjustedDp(12.dp)),
                text = screenName,
                fontSize = adjustedDp(48.dp),
                color = colorWhite
            )
        }
    }
}
