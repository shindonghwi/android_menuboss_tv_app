package com.orot.menuboss_tv.ui.screens.menu_board.widget

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

@Composable
fun ExoPlayerView(
    modifier: Modifier = Modifier,
    videoUrl: String,
    isScaleFit: Boolean,
    rotationDegrees: Float
) {
    val context = LocalContext.current
    val exoPlayer = rememberUpdatedState(ExoPlayer.Builder(context).build())

    val mediaItem = MediaItem.Builder()
        .setUri(videoUrl)
        .build()

    LaunchedEffect(exoPlayer.value) {
        exoPlayer.value.repeatMode = ExoPlayer.REPEAT_MODE_ALL
        exoPlayer.value.setMediaItem(mediaItem)
        exoPlayer.value.prepare()
        exoPlayer.value.playWhenReady = true
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
                                isScaleFit,
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
                                isScaleFit,
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

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.value.release()
        }
    }
}


private fun adjustTextureViewSize(
    textureView: TextureView,
    screenW: Int,
    screenH: Int,
    isScaleFit: Boolean,
    rotationDegrees: Float
) {
    val videoW = textureView.width
    val videoH = textureView.height

    val isVertical = rotationDegrees != 0f

    val videoAspectRatio = if (isVertical) videoH.toFloat() / videoW else videoW.toFloat() / videoH
    val screenAspectRatio = screenW.toFloat() / screenH

    val scaleX: Float
    val scaleY: Float

    if (isScaleFit) {
        if (isVertical) {  // 세로형
            val scaleFactor = screenW.toFloat() / videoW
            scaleX = scaleFactor
            scaleY = scaleFactor * videoAspectRatio
        } else {  // 가로형
            val scaleFactor = screenH.toFloat() / videoH
            scaleY = scaleFactor
            scaleX = videoW * scaleFactor / videoH
        }
    } else {
        if (videoAspectRatio > screenAspectRatio) {
            scaleY = screenH.toFloat() / videoH
            scaleX = scaleY * (videoH.toFloat() / videoW)
        } else {
            scaleX = screenW.toFloat() / videoW
            scaleY = scaleX * (videoW.toFloat() / videoH)
        }
    }

    val matrix = Matrix()
    matrix.setScale(scaleX, scaleY, videoW / 2f, videoH / 2f)
    textureView.setTransform(matrix)
}
