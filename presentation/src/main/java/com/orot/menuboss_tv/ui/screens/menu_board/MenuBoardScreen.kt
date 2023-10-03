package com.orot.menuboss_tv.ui.screens.menu_board

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.tv.material3.Text
import coil.compose.rememberAsyncImagePainter
import com.orot.menuboss_tv.domain.entities.DevicePlaylistModel
import com.orot.menuboss_tv.domain.entities.DeviceScheduleModel
import com.orot.menuboss_tv.ui.model.UiState
import com.orot.menuboss_tv.ui.navigations.LocalMainViewModel
import com.orot.menuboss_tv.ui.screens.menu_board.widget.ScheduleSlider
import com.orot.menuboss_tv.ui.screens.reload.ReloadScreen
import kotlinx.coroutines.delay
import java.time.LocalTime

@Composable
fun MenuBoardScreen() {
    val mainViewModel = LocalMainViewModel.current
    val screenState = mainViewModel.screenState.collectAsState().value

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
                when (item.data?.isPlaylist) {
                    true -> {
                        item.data.playlistModel?.let { PlaylistSlider(model = it) }
                    }
                    false -> {
                        item.data.scheduleModel?.let { ScheduleSlider(model = it) }
                    }
                    null -> ErrorBox("Error Retry")
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


@Composable
private fun PlaylistSlider(model: DevicePlaylistModel
){

}

//@Composable
//private fun PlaylistSlider(
//    items: DevicePlaylistModel?,
//) {
//
//    Crossfade(
//        targetState = items.isEmpty(),
//        animationSpec = tween(durationMillis = 1000, delayMillis = 2000),
//        label = "mainScreen"
//    ) { targetIsEmpty ->
//        if (targetIsEmpty) {
//            ReloadScreen()
//        } else {
//            ImageSwitcher(items = items)
//        }
//    }
//
//    if (items?.contents.isNullOrEmpty()) {
//        Text(text = "No Contents")
//    } else {
//        var currentIndex by remember { mutableIntStateOf(0) }
//        val contents = items?.contents
//
//        LaunchedEffect(Unit) {
//            while (true) {
//                currentIndex =
//                    (currentIndex + 1) % (contents?.size ?: 0)
//                delay(
//                    contents?.get(currentIndex)?.duration?.toLong()
//                        ?: 0L
//                )
//            }
//        }
//
//        Crossfade(
//            targetState = contents?.get(currentIndex)?.property?.imageUrl,
//            animationSpec = tween(durationMillis = 1500), label = ""
//        ) { item ->
//            Image(
//                painter = rememberAsyncImagePainter(item),
//                contentDescription = null,
//                contentScale = ContentScale.FillBounds,
//                modifier = Modifier.fillMaxSize()
//            )
//        }
//    }
//}
