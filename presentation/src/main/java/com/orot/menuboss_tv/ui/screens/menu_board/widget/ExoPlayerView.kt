package com.orot.menuboss_tv.ui.screens.menu_board.widget

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.lang.Float.max

@Composable
fun ExoPlayerView(
    modifier: Modifier = Modifier,
    videoUrl: String,
    contentScale: ContentScale,
    rotationDegrees: Float
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val exoPlayer = rememberUpdatedState(ExoPlayer.Builder(context).build())

    val mediaItem = MediaItem.Builder()
        .setUri(videoUrl)
        .build()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.value.pause()
                Lifecycle.Event.ON_RESUME -> {
                    if (exoPlayer.value.playbackState == ExoPlayer.STATE_IDLE ||
                        exoPlayer.value.playbackState == ExoPlayer.STATE_ENDED) {
                        // 준비되지 않았거나 재생이 끝난 경우, 다시 준비
                    } else if (exoPlayer.value.playbackState == ExoPlayer.STATE_READY) {
                        // 준비가 되어 있고 재생할 준비가 되어 있는 경우, 재생 시작
                        exoPlayer.value.playWhenReady = true
                    }
                }
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.value.release()
        }
    }

    LaunchedEffect(key1 = Unit) {
        exoPlayer.value.setMediaItem(mediaItem)
        exoPlayer.value.repeatMode = ExoPlayer.REPEAT_MODE_ALL
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            exoPlayer.value.prepare()
            exoPlayer.value.playWhenReady = true
        }
    }

    AndroidView(
        factory = { context ->
            TextureView(context).apply {
                surfaceTextureListener =
                    object : TextureView.SurfaceTextureListener {
                        override fun onSurfaceTextureSizeChanged(
                            surface: SurfaceTexture,
                            width: Int,
                            height: Int
                        ) {
                            adjustTextureViewSize(
                                this@apply,
                                width,
                                height,
                                contentScale,
                                rotationDegrees
                            )
                        }

                        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) =
                            Unit

                        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean =
                            true

                        override fun onSurfaceTextureAvailable(
                            surface: SurfaceTexture,
                            width: Int,
                            height: Int
                        ) {
                            exoPlayer.value.setVideoSurface(Surface(surface))
                            adjustTextureViewSize(
                                this@apply,
                                width,
                                height,
                                contentScale,
                                rotationDegrees
                            )
                        }
                    }
            }
        },
        modifier = modifier.then(
            Modifier.graphicsLayer {
                rotationZ = rotationDegrees
            }
        )
    )
}

private fun adjustTextureViewSize(
    textureView: TextureView,
    screenW: Int,
    screenH: Int,
    contentScale: ContentScale,
    rotationDegrees: Float
) {
    val videoW = textureView.width
    val videoH = textureView.height

    val isVertical = rotationDegrees != 0f
    val videoAspectRatio = if (isVertical) videoH.toFloat() / videoW else videoW.toFloat() / videoH

    var scaleX = 1.0f
    var scaleY = 1.0f

    when (contentScale) {
        ContentScale.FillBounds -> {
            if (isVertical) {
                // For vertical video, fill vertically
                scaleY = screenH.toFloat() / videoH
                scaleX = scaleY * (videoW.toFloat() / videoH)
            } else {
                // For horizontal video, fill horizontally
                scaleX = screenW.toFloat() / videoW
                scaleY = scaleX * (videoH.toFloat() / videoW)
            }
        }

        ContentScale.Crop -> {
            val aspectRatio = max(screenH.toFloat() / videoH, screenW.toFloat() / videoW)
            scaleX = aspectRatio / (screenW.toFloat() / videoW)
            scaleY = aspectRatio / (screenH.toFloat() / videoH)
        }

        ContentScale.Fit -> {
            if (videoAspectRatio > screenW.toFloat() / screenH) {
                // Fit vertically
                scaleY = screenH.toFloat() / videoH
                scaleX = scaleY * (videoW.toFloat() / videoH)
            } else {
                // Fit horizontally
                scaleX = screenW.toFloat() / videoW
                scaleY = scaleX * (videoH.toFloat() / videoW)
            }
        }
    }

    val matrix = Matrix()
    matrix.setScale(scaleX, scaleY, screenW / 2f, screenH / 2f)

    textureView.setTransform(matrix)
}
