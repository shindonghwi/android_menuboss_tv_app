package com.orot.menuboss_tv_kr.ui.screens.menu_board.widget

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView

@Composable
fun ExoPlayerView(
    modifier: Modifier = Modifier,
    videoUrl: String,
    contentScale: ContentScale,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val exoPlayer = rememberExoPlayer(Uri.parse(videoUrl))

    val resizeMode = when (contentScale) {
        ContentScale.Fit -> AspectRatioFrameLayout.RESIZE_MODE_FIT
        ContentScale.Crop -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        ContentScale.FillBounds -> AspectRatioFrameLayout.RESIZE_MODE_FILL
        else -> {
            Log.w("ExoPlayerView", "Unknown contentScale: $contentScale")
            AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
    }

    val mediaItem = MediaItem.Builder()
        .setUri(videoUrl)
        .build()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> {
                    if (exoPlayer.playbackState == ExoPlayer.STATE_IDLE ||
                        exoPlayer.playbackState == ExoPlayer.STATE_ENDED
                    ) {
                        // 준비되지 않았거나 재생이 끝난 경우, 다시 준비
                    } else if (exoPlayer.playbackState == ExoPlayer.STATE_READY) {
                        // 준비가 되어 있고 재생할 준비가 되어 있는 경우, 재생 시작
                        exoPlayer.playWhenReady = true
                    }
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    LaunchedEffect(key1 = Unit) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ALL
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                this.player = player
                this.resizeMode = resizeMode
                useController = false
            }
        },
        update = { playerView ->
            playerView.player = exoPlayer
            playerView.resizeMode = resizeMode
        },
        modifier = modifier
    )
}


@Composable
fun rememberExoPlayer(uri: Uri): ExoPlayer {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
            .also { player ->
                val mediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .build()
                player.setMediaItem(mediaItem)
                player.prepare()
            }
    }


    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    return exoPlayer
}
