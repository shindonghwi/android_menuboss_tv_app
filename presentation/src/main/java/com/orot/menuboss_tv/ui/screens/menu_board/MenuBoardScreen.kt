package com.orot.menuboss_tv.ui.screens.menu_board

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.orot.menuboss_tv.MainActivity
import com.orot.menuboss_tv.presentation.R
import com.orot.menuboss_tv.ui.model.UiState
import com.orot.menuboss_tv.ui.navigations.LocalMenuBoardViewModel
import com.orot.menuboss_tv.ui.navigations.LocalNavController
import com.orot.menuboss_tv.ui.navigations.RouteScreen
import com.orot.menuboss_tv.ui.screens.auth.AuthScreen
import com.orot.menuboss_tv.ui.screens.common.empty.EmptyContentScreen
import com.orot.menuboss_tv.ui.screens.common.expired.ExpiredScreen
import com.orot.menuboss_tv.ui.screens.common.reload.ReloadScreen
import com.orot.menuboss_tv.ui.screens.menu_board.widget.PlaylistSlider
import com.orot.menuboss_tv.ui.screens.menu_board.widget.ScheduleSlider
import com.orot.menuboss_tv.ui.theme.AdjustedBoldText
import com.orot.menuboss_tv.ui.theme.AdjustedMediumText
import com.orot.menuboss_tv.ui.theme.AdjustedSemiBoldText
import com.orot.menuboss_tv.ui.theme.colorBackground
import com.orot.menuboss_tv.utils.adjustedDp
import com.orotcode.menuboss.grpc.lib.PlayingEventRequest
import kotlinx.coroutines.launch

@Composable
fun MenuBoardScreen() {
    val tag = "MenuBoardScreen"
    val activity = LocalContext.current as MainActivity

    BackHandler { activity.finish()}

    val menuBoardViewModel = LocalMenuBoardViewModel.current
    val navController = LocalNavController.current
    val screenState = menuBoardViewModel.screenState.collectAsState().value
    val doAuthScreenActionState = menuBoardViewModel.navigateToAuthState.collectAsState().value

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val scope = rememberCoroutineScope()
    val resumedOnce = remember { mutableStateOf(false) }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Log.w(tag, "MenuBoardScreen: ON_CREATE")
                }

                Lifecycle.Event.ON_RESUME -> {
                    menuBoardViewModel.updateForeground(isForeground = true)
                    if (resumedOnce.value) {
                        scope.launch { menuBoardViewModel.sendEvent(PlayingEventRequest.PlayingEvent.RESUMED) }
                        Log.w(tag, "MenuBoardScreen: ON_RESUME")
                    } else {
                        resumedOnce.value = true
                    }
                }

                Lifecycle.Event.ON_PAUSE -> {
                    scope.launch { menuBoardViewModel.sendEvent(PlayingEventRequest.PlayingEvent.PAUSED) }
                    Log.w(tag, "MenuBoardScreen: ON_PAUSE")
                }

                Lifecycle.Event.ON_STOP -> {
                    menuBoardViewModel.updateForeground(isForeground = false)
                    scope.launch { menuBoardViewModel.sendEvent(PlayingEventRequest.PlayingEvent.STOPPED) }
                    Log.w(tag, "MenuBoardScreen: ON_STOP")
                }

                Lifecycle.Event.ON_DESTROY -> {
                    Log.w(tag, "MenuBoardScreen: ON_DESTROY")
                }

                else -> {}
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }


    LaunchedEffect(key1 = Unit, block = {
        menuBoardViewModel.startProcess()
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
            menuBoardViewModel.initState()
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
                                if (screenData?.playlistModel?.contents.isNullOrEmpty()) {
                                    EmptyContentScreen(modifier = Modifier.fillMaxSize())
                                } else {
                                    screenData?.playlistModel?.let { model -> PlaylistSlider(model = model) }
                                }
                            }

                            false -> {
                                if (screenData?.scheduleModel?.timeline.isNullOrEmpty()) {
                                    EmptyContentScreen(modifier = Modifier.fillMaxSize())
                                } else {
                                    screenData?.scheduleModel?.let { model -> ScheduleSlider(model = model) }
                                }
                            }

                            null -> EmptyContentScreen(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}
