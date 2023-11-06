package com.orot.menuboss_tv.ui.screens.menu_board

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.orot.menuboss_tv.MainActivity
import com.orot.menuboss_tv.ui.model.UiState
import com.orot.menuboss_tv.ui.navigations.LocalMainViewModel
import com.orot.menuboss_tv.ui.navigations.LocalNavController
import com.orot.menuboss_tv.ui.navigations.RouteScreen
import com.orot.menuboss_tv.ui.screens.auth.AuthScreen
import com.orot.menuboss_tv.ui.screens.common.reload.ReloadScreen
import com.orot.menuboss_tv.ui.screens.menu_board.widget.PlaylistSlider
import com.orot.menuboss_tv.ui.screens.menu_board.widget.ScheduleSlider
import com.orot.menuboss_tv.ui.source_pack.IconPack
import com.orot.menuboss_tv.ui.source_pack.iconpack.Logo
import com.orot.menuboss_tv.ui.theme.AdjustedBoldText
import com.orot.menuboss_tv.ui.theme.AdjustedMediumText
import com.orot.menuboss_tv.ui.theme.colorBackground
import com.orot.menuboss_tv.utils.adjustedDp

@Composable
fun MenuBoardScreen(
) {
    val activity = LocalContext.current as MainActivity
    val navController = LocalNavController.current
    val mainViewModel = LocalMainViewModel.current
    val screenState = mainViewModel.screenState.collectAsState().value
    val doAuthScreenActionState = mainViewModel.navigateToAuthState.collectAsState().value

    BackHandler { activity.finish() }

    LaunchedEffect(key1 = Unit, block = {
        mainViewModel.subscribeContentStream()
    })

    DisposableEffect(key1 = doAuthScreenActionState, effect = {
        if (doAuthScreenActionState) {
            navController.navigate(RouteScreen.AuthScreen.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
        onDispose {
            mainViewModel.initState()
        }
    })

    Crossfade(
        modifier = Modifier
            .fillMaxSize()
            .background(colorBackground),
        targetState = screenState,
        animationSpec = tween(
            durationMillis = 2000,
            delayMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = ""
    ) { item ->
        when (item) {
            is UiState.Idle,
            is UiState.Loading -> {
                ReloadScreen()
            }

            is UiState.Error -> {
                ReloadScreen()
            }

            is UiState.Success -> {
                val screenData = item.data

                if (screenData?.isDeleted == true) {
                    AuthScreen()
                } else if (screenData?.isExpired == true) {
                    ExpiredScreen(modifier = Modifier.fillMaxSize())
                } else {
                    Crossfade(
                        modifier = Modifier.fillMaxSize(),
                        targetState = screenData?.isPlaylist,
                        animationSpec = tween(
                            durationMillis = 2000,
                            delayMillis = 500,
                            easing = FastOutSlowInEasing
                        ),
                        label = ""
                    ) {
                        when (it) {
                            true -> {
                                screenData?.playlistModel?.let { model -> PlaylistSlider(model = model) }
                            }

                            false -> {
                                screenData?.scheduleModel?.let { model -> ScheduleSlider(model = model) }
                            }

                            null -> EmptyContentScreen(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyContentScreen(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(
                width = adjustedDp(150.dp),
                height = adjustedDp(75.dp),
            ), imageVector = IconPack.Logo, contentDescription = ""
        )
        AdjustedBoldText(
            modifier = Modifier.padding(top = adjustedDp(40.dp)),
            text = "No Content to display",
            fontSize = adjustedDp(24.dp),
        )

        AdjustedMediumText(
            modifier = Modifier.padding(top = adjustedDp(12.dp)),
            text = "MenuBoss Please register your schedule and playlist through the web or mobile",
            fontSize = adjustedDp(16.dp)
        )
    }
}

@Composable
private fun ExpiredScreen(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(
                width = adjustedDp(150.dp),
                height = adjustedDp(75.dp),
            ), imageVector = IconPack.Logo, contentDescription = ""
        )

        AdjustedBoldText(
            modifier = Modifier.padding(top = adjustedDp(40.dp)),
            text = "Your subscription has expired",
            fontSize = adjustedDp(24.dp),
        )

        AdjustedMediumText(
            modifier = Modifier.padding(top = adjustedDp(12.dp)),
            text = "You cannot use it because your subscription period has expired.",
            fontSize = adjustedDp(16.dp)
        )

        AdjustedMediumText(
            modifier = Modifier.padding(top = adjustedDp(4.dp)),
            text = "You can use it again by subscribing to the service",
            fontSize = adjustedDp(16.dp)
        )
    }
}