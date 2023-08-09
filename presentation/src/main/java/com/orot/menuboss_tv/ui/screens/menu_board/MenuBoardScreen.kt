@file:OptIn(ExperimentalFoundationApi::class)

package com.orot.menuboss_tv.ui.screens.menu_board

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MenuBoardScreen(menuBoardScreenViewModel: MenuBoardScreenViewModel) {

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

    ImageSlideshow(
        modifier = Modifier.fillMaxSize(),
        itemsCount = imageUrls.size,
        itemContent = { index ->
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current).data(imageUrls[index])
                    .crossfade(true).build(),
                contentDescription = null,
            )
        }
    )

}

@Composable
fun ImageSlideshow(
    modifier: Modifier = Modifier,
    autoSlideDuration: Long = 10000,
    itemsCount: Int,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 0, initialPageOffsetFraction = 0f) {
        Int.MAX_VALUE
    }
    // 지정한 시간마다 auto scroll.
    LaunchedEffect(key1 = pagerState.currentPage) {
        launch {
            while (true) {
                delay(autoSlideDuration)
                // 페이지 바뀌었다고 애니메이션이 멈추면 어색하니 NonCancellable
                withContext(NonCancellable) {
                    try {
                        // 일어날린 없지만 유저가 최대값 이상 스크롤 되었을때
                        if (pagerState.currentPage + 1 in 0..Int.MAX_VALUE) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            pagerState.animateScrollToPage(0)
                        }
                    } catch (e: Exception) {
                        pagerState.animateScrollToPage(0)
                    }

                }
            }
        }
    }


    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        userScrollEnabled = false,
        pageSize = PageSize.Fill,
        pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
            Orientation.Horizontal
        ),
    ) { page ->
        val index = page % itemsCount
        itemContent(index)
    }

}


