package com.orot.menuboss_tv.ui.screens.menu_board

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay

@Composable
fun MenuBoardScreen(menuBoardScreenViewModel: MenuBoardScreenViewModel) {

    LaunchedEffect(key1 = Unit, block = {
        menuBoardScreenViewModel.connectToWebSocket()
    })

    val imageUrls = listOf(
        "https://m.lgart.com/Down/Perf/202212/%EA%B0%80%EB%A1%9C%EA%B4%91%EA%B3%A01920x1080-3.jpg",
        "https://marketplace.canva.com/EAD2xI0GoM0/1/0/800w/canva-%ED%95%98%EB%8A%98-%EC%95%BC%EC%99%B8-%EC%9E%90%EC%97%B0-%EC%98%81%EA%B0%90-%EC%9D%B8%EC%9A%A9%EB%AC%B8-%EB%8D%B0%EC%8A%A4%ED%81%AC%ED%86%B1-%EB%B0%B0%EA%B2%BD%ED%99%94%EB%A9%B4-CQJp-Sw9JRs.jpg",
        "https://blog.kakaocdn.net/dn/9Yg2I/btqNJwwHIUS/WNhMAC34BopDSvpKmhy9X0/img.jpg",
    )

    ImageSlideshow(
        modifier = Modifier.fillMaxSize(),
        itemsCount = imageUrls.size,
        itemContent = { index ->
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrls[index])
                    .crossfade(true)
                    .build(),
                contentDescription = null,
            )
        }
    )

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlideshow(
    modifier: Modifier = Modifier,
    autoSlideDuration: Long = 3000,
    itemsCount: Int,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val pagerState = rememberPagerState()

    LaunchedEffect(pagerState.currentPage) {
        delay(autoSlideDuration)
        pagerState.animateScrollToPage((pagerState.currentPage + 1) % itemsCount)
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        count = itemsCount
    ) { page ->
        itemContent(page)
    }
}

