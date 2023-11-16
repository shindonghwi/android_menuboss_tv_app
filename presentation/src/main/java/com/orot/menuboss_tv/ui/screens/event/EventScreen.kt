package com.orot.menuboss_tv.ui.screens.event

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
import com.orot.menuboss_tv.presentation.R
import com.orot.menuboss_tv.ui.navigations.LocalMenuBoardViewModel
import com.orot.menuboss_tv.ui.theme.AdjustedBoldText
import com.orot.menuboss_tv.ui.theme.colorBackground
import com.orot.menuboss_tv.ui.theme.colorRed500
import com.orot.menuboss_tv.ui.theme.colorWhite
import com.orot.menuboss_tv.utils.adjustedDp
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EventScreen() {
    val menuBoardViewModel = LocalMenuBoardViewModel.current

    val event = menuBoardViewModel.eventCode.collectAsState(null).value
    val eventFlow = menuBoardViewModel.eventCode
    val screenName = menuBoardViewModel.screenName

    // 이벤트마다 고유한 키를 생성합니다.
    var eventKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        eventFlow.collectLatest { event ->
            event?.let {
                if (it == ContentEventResponse.ContentEvent.SHOW_SCREEN_NAME) {
                    eventKey++ // 고유한 키 값 업데이트
                }
            }
        }
    }

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = eventKey) {
        // 고유한 키가 변경될 때마다 UI 로직 실행
        visible = true
        delay(5000) // 5초 동안 표시
        visible = false
    }


    event?.let {
        when (it) {
            ContentEventResponse.ContentEvent.SHOW_SCREEN_NAME -> {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 1000)), // 페이드 인
                    exit = fadeOut(animationSpec = tween(durationMillis = 1000)) // 페이드 아웃
                ) {
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
            }

            else -> {}
        }
    }


}