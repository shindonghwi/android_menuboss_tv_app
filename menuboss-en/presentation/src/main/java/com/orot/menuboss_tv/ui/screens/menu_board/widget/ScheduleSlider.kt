package com.orot.menuboss_tv.ui.screens.menu_board.widget

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import coil.transform.Transformation
import com.orot.menuboss_tv.domain.entities.DeviceScheduleModel
import com.orot.menuboss_tv.ui.navigations.LocalMenuBoardViewModel
import com.orotcode.menuboss.grpc.lib.PlayingEventRequest
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


@Composable
fun ScheduleSlider(model: DeviceScheduleModel) {

    val menuBoardViewModel = LocalMenuBoardViewModel.current
    var currentTimeline by remember { mutableStateOf(getCurrentTimeline(model.timeline)) }
    val currentContent = currentTimeline?.playlist?.contents
    val isDirectionHorizontal =
        currentTimeline?.playlist?.property?.direction?.code == "Horizontal"
    val contentScale =
        when (currentTimeline?.playlist?.property?.fill?.code?.lowercase(Locale.getDefault())) {
            "fit" -> ContentScale.Fit
            "crop" -> ContentScale.Crop
            "stretch" -> ContentScale.FillBounds
            else -> ContentScale.Crop
        }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L) // 매 초마다 체크
            currentTimeline = getCurrentTimeline(model.timeline)
        }
    }

    currentContent?.let {
        var currentIndex by remember { mutableIntStateOf(0) }

        LaunchedEffect(currentContent, currentIndex) {
            menuBoardViewModel.run {
                currentTimeline?.playlist?.contents?.getOrNull(currentIndex)?.contentId?.let {
                    updateCurrentScheduleId(model.scheduleId)
                    updateCurrentPlaylistId(currentTimeline?.playlist?.playlistId)
                    updateCurrentContentId(it)
                    sendEvent(PlayingEventRequest.PlayingEvent.PLAYING)
                }
            }
            delay((it.getOrNull(currentIndex)?.duration?.times(1000L)) ?: 0L)

            // delay 이후에 currentIndex의 유효성 확인
            currentIndex = if (currentIndex >= it.size) {
                0
            } else {
                (currentIndex + 1) % it.size
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(
                targetState = Pair(it, currentIndex),
                animationSpec = tween(
                    durationMillis = 2000,
                    delayMillis = 500,
                    easing = FastOutSlowInEasing
                ), label = ""
            ) {
                it.first.forEachIndexed { index, content ->
                    when (content.type?.code) {
                        "Canvas", "Image" -> {
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

                                    AsyncImage(
                                        modifier = Modifier.fillMaxSize(),
                                        model = ImageRequest.Builder(
                                            LocalContext.current
                                        )
                                            .data(imageUrl)
                                            .scale(Scale.FILL)
                                            .transformations(object :
                                                Transformation {
                                                override val cacheKey: String get() = "$imageUrl$isDirectionHorizontal"

                                                override suspend fun transform(
                                                    input: Bitmap,
                                                    size: Size
                                                ): Bitmap {
                                                    val matrix = Matrix()
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
                                        contentDescription = null,
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
}


private fun getCurrentTimeline(timelines: List<DeviceScheduleModel.Timeline>?): DeviceScheduleModel.Timeline? {
    val currentTime = getCurrentTime() // 현재 시간을 얻어옴
    val currentTimeMinutes = currentTime.toMinutes()

    if (timelines.isNullOrEmpty()) return null

    // timeline이 하나만 있는 경우
    if (timelines.size == 1) return timelines[0]

    // 두 번째 timeline부터 확인
    val matchedTimeline = timelines.drop(1).firstOrNull {
        val startTime = it.time?.start?.toHourMinute()?.toMinutes() ?: 0
        val endTime = it.time?.end?.toHourMinute()?.toMinutes() ?: 0

        currentTimeMinutes in startTime until endTime
    }

    // 못찾았다면 0번째 timeline 반환
    return matchedTimeline ?: timelines[0]
}

private fun Pair<Int, Int>.toMinutes(): Int = this.first * 60 + this.second

private fun getCurrentTime(): Pair<Int, Int> {
    val currentLocale = Locale.getDefault()
    val currentTimeZone = TimeZone.getDefault()

    val calendar = Calendar.getInstance(currentTimeZone, currentLocale)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    return Pair(hour, minute)
}

private fun String.toHourMinute(): Pair<Int, Int> {
    val parts = this.split(":")
    if (parts.size != 2) return Pair(0, 0)

    return Pair(parts[0].toInt(), parts[1].toInt())
}