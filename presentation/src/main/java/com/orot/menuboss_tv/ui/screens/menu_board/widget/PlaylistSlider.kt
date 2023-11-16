package com.orot.menuboss_tv.ui.screens.menu_board.widget

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.tv.material3.Text
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.Transformation
import com.orot.menuboss_tv.domain.entities.DevicePlaylistModel
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun PlaylistSlider(model: DevicePlaylistModel) {
    val contents = model.contents
    val isDirectionHorizontal = model.property?.direction?.code == "Horizontal"
    val contentScale = when(model.property?.fill?.code?.lowercase(Locale.getDefault())){
        "fit" -> ContentScale.Fit
        "crop" -> ContentScale.Crop
        "stretch" -> ContentScale.FillBounds
        else -> ContentScale.Crop
    }

    contents?.let {
        var currentIndex by remember { mutableIntStateOf(0) }

        LaunchedEffect(currentIndex) {
            while (true) {
                delay(
                    (it.getOrNull(currentIndex)?.duration?.times(1000L)) ?: 0L
                )

                // delay 이후에 currentIndex의 유효성 확인
                currentIndex = if (currentIndex >= it.size) {
                    0
                } else {
                    (currentIndex + 1) % it.size
                }
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            it.forEachIndexed { index, content ->
                when (content.type?.code) {
                    "Image" -> {
                        Crossfade(
                            targetState = currentIndex == index,
                            animationSpec = tween(
                                durationMillis = 2000,
                                delayMillis = 500,
                                easing = FastOutSlowInEasing
                            ),
                            label = ""
                        ) { isCurrent ->
                            if (isCurrent) {
                                val imageUrl = content.property?.imageUrl

                                val painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imageUrl)
                                        .size(Size.ORIGINAL)
                                        .transformations(object :
                                            Transformation {
                                            override val cacheKey: String get() = "$imageUrl$isDirectionHorizontal"

                                            override suspend fun transform(
                                                input: Bitmap,
                                                size: Size
                                            ): Bitmap {
                                                val matrix = android.graphics.Matrix()
                                                matrix.postRotate(if (isDirectionHorizontal) 0f else -90f)
                                                return Bitmap.createBitmap(
                                                    input,
                                                    0,
                                                    0,
                                                    input.width,
                                                    input.height,
                                                    matrix,
                                                    true
                                                )
                                            }
                                        })
                                        .build(),
                                )

                                Image(
                                    modifier = Modifier.fillMaxSize(),
                                    painter = painter,
                                    contentDescription = null,
                                    contentScale = contentScale
                                )
                            }
                        }
                    }

                    "Video" -> {
                        Crossfade(
                            targetState = currentIndex == index,
                            animationSpec = tween(
                                durationMillis = 1000,
                                delayMillis = 500,
                                easing = FastOutSlowInEasing
                            ),
                            label = ""
                        ) { isCurrent ->
                            if (isCurrent) {
                                ExoPlayerView(
                                    modifier = Modifier.fillMaxSize(),
                                    videoUrl = content.property?.videoUrl.toString(),
                                    contentScale = contentScale,
                                    rotationDegrees = if (isDirectionHorizontal) 0f else -90f
                                )
                            }
                        }
                    }

                    else -> Text("Not Supported")
                }
            }
        }
    }
}
