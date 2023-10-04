package com.orot.menuboss_tv.ui.screens.menu_board

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.tv.material3.Text
import coil.compose.rememberAsyncImagePainter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.orot.menuboss_tv.domain.entities.DevicePlaylistModel
import com.orot.menuboss_tv.ui.model.UiState
import com.orot.menuboss_tv.ui.navigations.LocalMainViewModel
import com.orot.menuboss_tv.ui.screens.menu_board.widget.ScheduleSlider
import com.orot.menuboss_tv.ui.screens.reload.ReloadScreen
import kotlinx.coroutines.delay

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
private fun PlaylistSlider(
    model: DevicePlaylistModel
) {
    val contents = model.contents
    val isDirectionHorizontal = model.property?.direction?.code == "Horizontal"
    val isScaleFit = model.property?.fill?.code == "Fit"

    val aspectRatio = if (isDirectionHorizontal) 16f / 9f else 9f / 16f
    val contentScale = if (isScaleFit) ContentScale.Fit else ContentScale.Crop

    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentIndex) {
        while (true) {
            delay((contents?.get(currentIndex)?.duration?.times(1000L)) ?: 0L)
            currentIndex = (currentIndex + 1) % (contents?.size ?: 1)
        }
    }

    BoxWithConstraints(
        modifier = Modifier.aspectRatio(aspectRatio),
        contentAlignment = Alignment.Center
    ) {
        contents?.forEachIndexed { index, content ->
            Box(modifier = Modifier.fillMaxSize()) { // 추가된 부분
                when (content.type?.code) {
                    "Image" -> {
                        Crossfade(
                            targetState = currentIndex == index,
                            animationSpec = tween(
                                durationMillis = 1000,
                                delayMillis = 2000
                            ),
                            label = ""
                        ) { isCurrent ->
                            if (isCurrent) {
                                Image(
                                    painter = rememberAsyncImagePainter(content.property?.imageUrl),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = contentScale,
                                )
                            }
                        }
                    }

                    "Video" -> {
                        Crossfade(
                            targetState = currentIndex == index,
                            animationSpec = tween(
                                durationMillis = 1000,
                                delayMillis = 2000
                            ),
                            label = ""
                        ) { isCurrent ->
                            if (isCurrent) {
                                ExoPlayerView(
                                    modifier = Modifier.fillMaxSize(),
                                    videoUrl = content.property?.videoUrl.toString(),
                                    contentScale = contentScale
                                )
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}

@Composable
fun ExoPlayerView(
    modifier: Modifier = Modifier,
    videoUrl: String,
    contentScale: ContentScale = ContentScale.Fit
) {
    val context = LocalContext.current

    val exoPlayer = rememberUpdatedState(ExoPlayer.Builder(context).build())

    val mediaItem = MediaItem.Builder()
        .setUri(videoUrl)
        .build()

    LaunchedEffect(exoPlayer.value) {
        exoPlayer.value.setMediaItem(mediaItem)
        exoPlayer.value.prepare()
        exoPlayer.value.playWhenReady = true
    }

    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer.value
                useController = false
                resizeMode = when (contentScale) {
                    ContentScale.Crop -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    ContentScale.Fit -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                    else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            }
        },
        modifier = modifier
    )

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.value.release()
        }
    }

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
