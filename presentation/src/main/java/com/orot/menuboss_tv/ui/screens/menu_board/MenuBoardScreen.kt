package com.orot.menuboss_tv.ui.screens.menu_board

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.orot.menuboss_tv.ui.screens.reload.ReloadScreen
import kotlinx.coroutines.delay
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse as ConnectEventResponse1

@Composable
fun MenuBoardScreen(
) {

    val items = listOf<Pair<Long,String>>()

    Crossfade(
        targetState = items.isEmpty(),
        animationSpec = tween(durationMillis = 1000, delayMillis = 2000),
        label = "mainScreen"
    ) { targetIsEmpty ->
        if (targetIsEmpty) {
            ReloadScreen()
        } else {
            ImageSwitcher(items = items)
        }
    }
}

@Composable
private fun ImageSwitcher(
    items: List<Pair<Long, String>>,
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            currentIndex = (currentIndex + 1) % items.size
            delay(items[currentIndex].first)
        }
    }

    Crossfade(
        targetState = items[currentIndex].second,
        animationSpec = tween(durationMillis = 1500), label = ""
    ) { item ->
        Image(
            painter = rememberAsyncImagePainter(item),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }
}
