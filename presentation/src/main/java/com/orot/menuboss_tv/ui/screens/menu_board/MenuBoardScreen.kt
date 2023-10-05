package com.orot.menuboss_tv.ui.screens.menu_board

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.tv.material3.Text
import com.orot.menuboss_tv.ui.model.UiState
import com.orot.menuboss_tv.ui.navigations.LocalMainViewModel
import com.orot.menuboss_tv.ui.navigations.LocalNavController
import com.orot.menuboss_tv.ui.navigations.RouteScreen
import com.orot.menuboss_tv.ui.screens.menu_board.widget.PlaylistSlider
import com.orot.menuboss_tv.ui.screens.menu_board.widget.ScheduleSlider
import com.orot.menuboss_tv.ui.screens.reload.ReloadScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

@Composable
fun MenuBoardScreen() {

    val navController = LocalNavController.current
    val mainViewModel = LocalMainViewModel.current
    val screenState = mainViewModel.screenState.collectAsState().value
    val deviceState = mainViewModel.deviceState.collectAsState().value

    LaunchedEffect(key1 = screenState){
        when (screenState) {
            is UiState.Success -> {
                val screenData = screenState.data
                if (screenData?.isDeleted == true) {
                    mainViewModel.requestGetDeviceInfo()
                }
            }else->{}
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
                ErrorBox("Error Retry")
            }

            is UiState.Success -> {
                val screenData = item.data

                if (screenData?.isDeleted == true) {
                    Text("Screen Deleted")
                } else if (screenData?.isExpired == false) {
                    Text("Screen Expired")
                } else {
                    when (screenData?.isPlaylist) {
                        true -> {
                            screenData.playlistModel?.let { PlaylistSlider(model = it) }
                        }

                        false -> {
                            screenData.scheduleModel?.let { ScheduleSlider(model = it) }
                        }

                        null -> Text("Empty Contents")
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorBox(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message)
    }
}

