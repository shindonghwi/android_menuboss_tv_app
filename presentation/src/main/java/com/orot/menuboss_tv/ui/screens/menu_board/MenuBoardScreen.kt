package com.orot.menuboss_tv.ui.screens.menu_board

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import com.orot.menuboss_tv.ui.model.UiState
import com.orot.menuboss_tv.ui.navigations.LocalMainViewModel
import com.orot.menuboss_tv.ui.navigations.LocalNavController
import com.orot.menuboss_tv.ui.navigations.RouteScreen
import com.orot.menuboss_tv.ui.screens.menu_board.widget.PlaylistSlider
import com.orot.menuboss_tv.ui.screens.menu_board.widget.ScheduleSlider
import com.orot.menuboss_tv.ui.screens.reload.ReloadScreen
import com.orot.menuboss_tv.ui.source_pack.IconPack
import com.orot.menuboss_tv.ui.source_pack.iconpack.Logo
import com.orot.menuboss_tv.ui.theme.AdjustedBoldText
import com.orot.menuboss_tv.ui.theme.AdjustedMediumText
import com.orot.menuboss_tv.utils.adjustedDp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

@Composable
fun MenuBoardScreen() {

    val navController = LocalNavController.current
    val mainViewModel = LocalMainViewModel.current
    val screenState = mainViewModel.screenState.collectAsState().value
    val deviceState = mainViewModel.deviceState.collectAsState().value

    LaunchedEffect(key1 = screenState) {
        when (screenState) {
            is UiState.Success -> {
                val screenData = screenState.data
                if (screenData?.isDeleted == true) {
                    mainViewModel.requestGetDeviceInfo()
                }
            }

            else -> {}
        }
    }

    LaunchedEffect(deviceState) {
        if (deviceState is UiState.Success) {
            if (deviceState.data?.status == "Unlinked") {
                mainViewModel.run { subscribeConnectStream() }

                val code = deviceState.data.linkProfile?.pinCode ?: ""
                val qrUrl = deviceState.data.linkProfile?.qrUrl ?: ""
                val encodedQrUrl = withContext(Dispatchers.IO) {
                    URLEncoder.encode(qrUrl, "UTF-8")
                }

                navController.navigate("${RouteScreen.AuthScreen.route}/$code/$encodedQrUrl") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        }
    }

    Crossfade(
        targetState = screenState,
        animationSpec = tween(durationMillis = 1000), label = ""
    ) { item ->
        when (item) {
            is UiState.Idle,
            is UiState.Loading -> {
                ReloadScreen()
            }

            is UiState.Error -> {
                ReloadScreen()
            }

            is UiState.Success -> {
                val screenData = item.data

                if (screenData?.isDeleted == true) {
                    ReloadScreen()
                } else if (screenData?.isExpired == false) {
                    ExpiredScreen(modifier = Modifier.fillMaxSize())
                } else {
                    when (screenData?.isPlaylist) {
                        true -> {
                            screenData.playlistModel?.let { PlaylistSlider(model = it) }
                        }

                        false -> {
                            screenData.scheduleModel?.let { ScheduleSlider(model = it) }
                        }

                        null -> EmptyContentScreen(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyContentScreen(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = modifier.size(
                width = adjustedDp(150.dp),
                height = adjustedDp(75.dp),
            ), imageVector = IconPack.Logo, contentDescription = ""
        )
        AdjustedBoldText(
            modifier = Modifier.padding(top = adjustedDp(40.dp)),
            text = "No Content to display",
            fontSize = adjustedDp(24.dp),
        )

        AdjustedMediumText(
            modifier = Modifier.padding(top = adjustedDp(12.dp)),
            text = "MenuBoss Please register your schedule and playlist through the web or mobile",
            fontSize = adjustedDp(16.dp)
        )
    }
}

@Composable
private fun ExpiredScreen(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = modifier.size(
                width = adjustedDp(150.dp),
                height = adjustedDp(75.dp),
            ), imageVector = IconPack.Logo, contentDescription = ""
        )

        AdjustedBoldText(
            modifier = Modifier.padding(top = adjustedDp(40.dp)),
            text = "Your subscription has expired",
            fontSize = adjustedDp(24.dp),
        )

        AdjustedMediumText(
            modifier = Modifier.padding(top = adjustedDp(12.dp)),
            text = "You cannot use it because your subscription period has expired.",
            fontSize = adjustedDp(16.dp)
        )

        AdjustedMediumText(
            modifier = Modifier.padding(top = adjustedDp(4.dp)),
            text = "You can use it again by subscribing to the service",
            fontSize = adjustedDp(16.dp)
        )
    }
}