package com.orot.menuboss_tv.ui.screens.menu_board.widget

import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.tv.material3.Text
import coil.compose.rememberAsyncImagePainter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.orot.menuboss_tv.domain.entities.DeviceScheduleModel
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun ScheduleSlider(model: DeviceScheduleModel) {
    val currentTimeline = getCurrentTimeline(model.timeline)
    val currentContent = currentTimeline?.playlist?.contents

    currentContent?.let {
        var currentIndex by remember { mutableIntStateOf(0) }

        LaunchedEffect(currentContent) {
            while (true) {
                delay((it[currentIndex].duration?.times(1000L)) ?: 0L)
                currentIndex = (currentIndex + 1) % it.size
            }
        }

        val contentType = it[currentIndex].type?.code

        Crossfade(
            targetState = it[currentIndex].property?.imageUrl.toString(),
            animationSpec = tween(durationMillis = 1500), label = ""
        ) { contentUri ->
            Image(
                painter = rememberAsyncImagePainter(contentUri),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
//            when (contentType) {
//                "Image" -> {
//                    Image(
//                        painter = rememberAsyncImagePainter(contentUri),
//                        contentDescription = null,
//                        contentScale = ContentScale.FillBounds,
//                        modifier = Modifier.fillMaxSize()
//                    )
//                }
//
//                "Video" -> {
//                    VideoPlayer(
//                        uri = contentUri,
//                    )
//                }
//
//                else -> Text("Unsupported content type")
//            }
        }
    } ?: run {
        Text("No Content Available")
    }
}


@Composable
fun VideoPlayer(uri: String) {

    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .also { player ->
                val mediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .build()
                player.setMediaItem(mediaItem)
                player.prepare()
                player.playWhenReady = true
            }
    }

    AndroidView(
        factory = { context ->
            StyledPlayerView(context).apply {
                player = exoPlayer
                useController = false
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { view ->
            // If any update logic is needed later
        }
    )

    DisposableEffect(uri) {
        onDispose { exoPlayer.release() }
    }
}

private fun getCurrentTimeline(timelines: List<DeviceScheduleModel.Timeline>?): DeviceScheduleModel.Timeline? {
    val currentTime = getCurrentTimeString()

    val matchedTimeline = if (timelines?.size == 1) {
        timelines[0]
    } else {
        timelines?.drop(1)?.firstOrNull {
            val startTime = it.time?.start ?: "00:00"
            val endTime = it.time?.end ?: "00:00"

            currentTime in startTime..endTime
        } ?: timelines?.firstOrNull()
    }

    return matchedTimeline
}

private fun getCurrentTimeString(): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
    val minute = calendar.get(Calendar.MINUTE).toString().padStart(2, '0')
    return "$hour:$minute"
}
