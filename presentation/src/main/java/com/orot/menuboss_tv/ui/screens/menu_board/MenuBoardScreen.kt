package com.orot.menuboss_tv.ui.screens.menu_board

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.orot.menuboss_tv.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun MenuBoardScreen(
    menuBoardScreenViewModel: MenuBoardScreenViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = Unit, block = {
        menuBoardScreenViewModel.connectToWebSocket()
    })

    val imageUrls = listOf(
        "https://m.lgart.com/Down/Perf/202212/%EA%B0%80%EB%A1%9C%EA%B4%91%EA%B3%A01920x1080-3.jpg",
        "https://marketplace.canva.com/EAD2xI0GoM0/1/0/800w/canva-%ED%95%98%EB%8A%98-%EC%95%BC%EC%99%B8-%EC%9E%90%EC%97%B0-%EC%98%81%EA%B0%90-%EC%9D%B8%EC%9A%A9%EB%AC%B8-%EB%8D%B0%EC%8A%A4%ED%81%AC%ED%86%B1-%EB%B0%B0%EA%B2%BD%ED%99%94%EB%A9%B4-CQJp-Sw9JRs.jpg",
        "https://blog.kakaocdn.net/dn/9Yg2I/btqNJwwHIUS/WNhMAC34BopDSvpKmhy9X0/img.jpg",
        "https://mblogthumb-phinf.pstatic.net/MjAxOTA3MjlfMjAx/MDAxNTY0NDAxNjEzNDgy.jrcSPgSZ1C52bTn0Lt9fhdX7qFPUts6qI7bp17GcjVsg.CfQRIEKV2qNwFFH-29TuveeZhB5PtgjyRzZoQ0dessUg.JPEG.msme3/940581-popular-disney-wallpaper-for-computer-1920x1080-for-iphone-5.jpg?type=w800",
        "https://www.10wallpaper.com/wallpaper/2560x1600/1702/Sea_dawn_nature_sky-High_Quality_Wallpaper_2560x1600.jpg",
        "https://blog.kakaocdn.net/dn/daPJMD/btqCinzhh9J/akDK6BMiG3QKH3XWXwobx1/img.jpg",
    )

    ImageSwitcher(
        images = imageUrls,
        intervalMillis = 3000,
        crossfadeDurationMillis = 1500
    )
}

@Composable
private fun ImageSwitcher(images: List<String>, intervalMillis: Long, crossfadeDurationMillis: Int) {
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(intervalMillis)
            currentIndex = (currentIndex + 1) % images.size
        }
    }

    Crossfade(
        targetState = images[currentIndex],
        animationSpec = tween(durationMillis = crossfadeDurationMillis), label = ""
    ) { imageUrl ->
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }
}
