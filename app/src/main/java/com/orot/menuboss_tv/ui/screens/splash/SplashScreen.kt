package com.orot.menuboss_tv.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.orot.menuboss_tv.R
import com.orot.menuboss_tv.ui.navigations.LocalNavController
import com.orot.menuboss_tv.ui.theme.colorBackground
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {
    val navController = LocalNavController.current


    LaunchedEffect(Unit) {
        delay(3000L)
        navController.navigate("/auth")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorBackground),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(250.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo"
        )
    }
}